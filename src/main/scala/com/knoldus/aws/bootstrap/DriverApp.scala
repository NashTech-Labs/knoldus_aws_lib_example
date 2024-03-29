package com.knoldus.aws.bootstrap

import akka.Done
import akka.actor.CoordinatedShutdown.Reason
import akka.actor.{ ActorSystem, CoordinatedShutdown }
import akka.http.scaladsl.Http
import akka.stream.Materializer
import com.knoldus.aws.models.kinesis.BankAccountTable
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ Await, ExecutionContextExecutor, Future }
import scala.util.{ Failure, Success }
import com.knoldus.aws.services.kinesis.processor.BankAccountEventConsumer
import software.amazon.awssdk.regions.Region

object DriverApp extends App with LazyLogging {
  lazy val conf = ConfigFactory.load()
  implicit val actorSystem: ActorSystem = ActorSystem("knoldus-aws-lib-example-system", conf)
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = global
  val akkaShutdown = CoordinatedShutdown(actorSystem)

  try {
    val httpServerConfig = conf.getConfig("http")
    val services = new ServiceInstantiator(conf)
    val httpServer = new HttpServer(httpServerConfig)(
      system = actorSystem,
      executionContext = executionContext,
      materializer = materializer
    )

    val routes = new RoutesInstantiator(services).routes
    val serverBinding: Future[Http.ServerBinding] = httpServer.start(routes).andThen {
      case Failure(ex) => shutdown(ex)
    }

    akkaShutdown.addTask(CoordinatedShutdown.PhaseServiceUnbind, "Unbinding http server") { () =>
      serverBinding.transformWith {
        case Success(binding) =>
          binding.unbind().andThen {
            case Success(_) => logger.info("Has unbounded http server.")
            case Failure(ex) => logger.error(s"Has failed to unbind http server: $ex")
          }
        case Failure(_) => Future.successful(Done)
      }
    }

    /**
      * Specific to Bank Account Event Consumer and Processor
      */
    val streamName = conf.getString("data-stream-name")
    val consumerApplicationName = conf.getString("consumer-application-name")
    val region = Region.of(conf.getString("aws-region"))
    val bankAccountTableName = conf.getString("bank-account-table-name")

    val bankAccountTable = BankAccountTable(bankAccountTableName)

    val bankAccountEventConsumer =
      new BankAccountEventConsumer(streamName, consumerApplicationName, region, bankAccountTable)

    // starting the consumer
    bankAccountEventConsumer.run()

  } catch {
    case e: Throwable =>
      Await.result(shutdown(e), 30.seconds)
  }

  private def shutdown(e: Throwable): Future[Done] = {
    logger.error("Error starting application:")
    akkaShutdown.run(new Reason {
      override def toString: String = "Error starting application: " ++ e.getMessage
    })
  }
}

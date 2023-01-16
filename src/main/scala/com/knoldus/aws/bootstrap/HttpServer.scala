package com.knoldus.aws.bootstrap

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class HttpServer(httpConfig: Config)(implicit
  system: ActorSystem,
  executionContext: ExecutionContext,
  materializer: Materializer
) extends LazyLogging {

  def start(routes: Route): Future[Http.ServerBinding] = {
    val interface = httpConfig.getString("interface")
    val port = httpConfig.getInt("port")
    val prefix = httpConfig.getString("prefix")

    val prefixedRoutes = pathPrefix(prefix).apply(routes)

    val serverBinding = Http().newServerAt(interface, port).bind(prefixedRoutes)

    serverBinding.onComplete {
      case Success(_) =>
        logger.info("Has bound server on {}:{}.", interface, port)
      case Failure(ex) =>
        logger.error(s"Has failed to bind to {}:{}, reason: $ex", interface, port)
    }
    serverBinding
  }

}

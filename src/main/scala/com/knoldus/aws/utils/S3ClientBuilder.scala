package com.knoldus.aws.utils

import com.amazonaws.auth.{ AWSCredentialsProvider, DefaultAWSCredentialsProviderChain }
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
import com.knoldus.aws.utils.Constants.LOCALSTACK
import com.knoldus.common.aws.CredentialsLookup
import com.knoldus.s3.models.Configuration
import com.knoldus.s3.services.S3Service.buildAmazonS3Client

object S3ClientBuilder {

  def s3ClientBuilder(s3config: Configuration): AmazonS3 =
    if (s3config.s3Config.simpleStorageServiceEndpoint.equals(LOCALSTACK))
      AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(
          new EndpointConfiguration(s3config.s3Config.simpleStorageServiceEndpoint, s3config.awsConfig.awsRegion)
        )
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .withPathStyleAccessEnabled(true)
        .build()
    else {
      val credentials: AWSCredentialsProvider =
        CredentialsLookup.getCredentialsProvider(s3config.awsConfig.awsAccessKey, s3config.awsConfig.awsSecretKey)
      buildAmazonS3Client(s3config, credentials)
    }
}

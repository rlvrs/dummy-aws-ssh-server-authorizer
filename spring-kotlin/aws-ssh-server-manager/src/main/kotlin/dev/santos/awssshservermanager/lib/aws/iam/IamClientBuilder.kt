package dev.santos.awssshservermanager.lib.aws.iam

import dev.santos.awssshservermanager.lib.aws.config.AwsIamConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.iam.IamClient

@Component
class IamClientBuilder(@Autowired private val awsIamConfig: AwsIamConfig) {

  fun buildClient(awsCredentials: AwsCredentials): IamClient {
    val builder = IamClient
      .builder()
      .region(awsIamConfig.region)
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(awsCredentials.accessKeyId(), awsCredentials.secretAccessKey())
        )
      )
    return when (awsIamConfig.endpointUri) {
      null -> builder
      else -> builder.endpointOverride(awsIamConfig.endpointUri)
    }.build()
  }
}

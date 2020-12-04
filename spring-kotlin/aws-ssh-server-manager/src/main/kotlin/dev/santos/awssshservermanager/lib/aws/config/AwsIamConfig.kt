package dev.santos.awssshservermanager.lib.aws.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import java.net.URI

data class AwsIamConfig(
  val region: Region,
  val endpointUri: URI?,
  val path: String
)

@Configuration
class AwsIamConfiguration {
  @Value("\${aws.iam.region}")
  private val regionStr: String? = null

  @Value("\${aws.iam.endpointUri}")
  private val endpointUriStr: String? = null

  @Value("\${aws.iam.path}")
  val path: String? = null

  @Bean
  fun awsIamConfig(): AwsIamConfig {
    return when (endpointUriStr) {
      null -> AwsIamConfig(Region.of(regionStr), null, path.orEmpty())
      else -> AwsIamConfig(Region.of(regionStr), URI(endpointUriStr), path.orEmpty())
    }
  }
}

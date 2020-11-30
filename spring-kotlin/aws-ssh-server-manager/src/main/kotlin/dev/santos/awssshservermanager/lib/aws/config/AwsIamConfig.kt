package dev.santos.awssshservermanager.lib.aws.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.iam.IamClient
import java.net.URI

data class AwsIamConfig(
  val region: Region,
  val endpointUri: URI?,
  val path: String
) {
    fun buildClient(awsCredentials: AwsCredentials): IamClient {
        val builder = IamClient
                .builder()
                .region(this.region)
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsCredentials.accessKeyId(), awsCredentials.secretAccessKey())
                    )
                )
        return when (this.endpointUri) {
            null -> builder
            else -> builder.endpointOverride(this.endpointUri)
        }.build()
    }
}

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

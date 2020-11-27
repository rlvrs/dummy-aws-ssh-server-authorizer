package dev.santos.awssshservermanager.lib.aws.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient
import java.net.URI

@Configuration
class AwsSsmConfig {
    @Value("\${aws.ssm.region}")
    private val regionStr: String? = null

    @Value("\${aws.ssm.endpointUri}")
    private val endpointUriStr: String? = null

    @Value("\${aws.ssm.parameterPrefix}")
    val parameterPrefix: String? = null

    @Bean
    fun parameterPrefix(): String {
        return when (parameterPrefix) {
            null -> ""
            else -> parameterPrefix!!
        }
    }

    @Bean
    fun ssmClient(): SsmClient {
        val region = Region.of(regionStr)
        return when (endpointUriStr) {
            null -> SsmClient
                        .builder()
                        .region(region)
                        .build()
            else -> SsmClient
                        .builder()
                        .region(region)
                        .endpointOverride(URI(endpointUriStr))
                        .build()
            }
    }
}

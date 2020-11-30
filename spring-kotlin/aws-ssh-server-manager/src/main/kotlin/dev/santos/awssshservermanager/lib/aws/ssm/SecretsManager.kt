package dev.santos.awssshservermanager.lib.aws.ssm

import dev.santos.awssshservermanager.lib.aws.config.AwsSsmConfig
import dev.santos.awssshservermanager.lib.aws.exception.DuplicateSecretException
import dev.santos.awssshservermanager.lib.aws.exception.SecretNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.*

@Component
class SecretsManager(@Autowired private val ssmClient: SsmClient, @Autowired private val awsSsmConfig: AwsSsmConfig) {
    fun getSecret(name: String): String {
        val request = GetParameterRequest
            .builder()
            .name(name)
            .withDecryption(true)
            .build()

        try {
            val response = ssmClient.getParameter(request)
            return response.parameter().value()
        } catch (e: ParameterNotFoundException) {
            throw SecretNotFoundException(e.message.orEmpty())
        }
    }

    fun saveSecret(name: String, value: String): String {
        val prefixedName = "${awsSsmConfig.parameterPrefix}/${name}"
        val request = PutParameterRequest
            .builder()
            .name(prefixedName)
            .value(value)
            .type(ParameterType.SECURE_STRING)
            .build()
        try {
            ssmClient.putParameter(request)
            return prefixedName
        } catch (e: ParameterAlreadyExistsException) {
            throw DuplicateSecretException(e.message.orEmpty())
        } catch (e: Exception) {
            throw e
        }
    }
}
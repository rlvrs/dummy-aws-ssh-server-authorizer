package dev.santos.awssshservermanager.lib.aws.ssm

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import dev.santos.awssshservermanager.lib.aws.exception.DuplicateSecretException
import dev.santos.awssshservermanager.lib.aws.exception.SecretNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class, LocalstackDockerExtension::class)
@LocalstackDockerProperties(
        services = ["ssm"],
        pullNewImage = false,
        imageTag = "0.12.2",
        useSingleDockerContainer = false
)
class SecretsManagerShould {
    init {
        System.setProperty("aws.accessKeyId", "0")
        System.setProperty("aws.secretAccessKey", "0")
    }

    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var secretsManager: SecretsManager

    @Test
    fun `save the secret with the prefix`() {
        val secretName = "new_secret"
        val secretValue = "super_secret_value"
        secretsManager.saveSecret(secretName, secretValue)
        val value = secretsManager.getSecret("/test/dev/prefix/${secretName}")
        Assertions.assertEquals(value, secretValue)
    }

    @Test
    fun `throw an exception if secret already exists`() {
        val secretName = "duplicate_secret"
        val secretValue = "super_secret_value"
        secretsManager.saveSecret(secretName, secretValue)
        Assertions.assertThrows(DuplicateSecretException::class.java) {
            secretsManager.saveSecret(secretName, secretValue)
        }
    }

    @Test
    fun `throw an exception if secret does not exist`() {
        val secretName = "unknown_secret"
        Assertions.assertThrows(SecretNotFoundException::class.java) {
            secretsManager.getSecret("/test/dev/prefix/${secretName}")
        }
    }
}

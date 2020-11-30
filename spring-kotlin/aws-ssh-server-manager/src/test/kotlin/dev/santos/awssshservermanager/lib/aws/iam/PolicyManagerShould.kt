package dev.santos.awssshservermanager.lib.aws.iam

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import dev.santos.awssshservermanager.helper.ResourceHelper
import dev.santos.awssshservermanager.lib.aws.exception.DuplicatePolicyException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

@SpringBootTest
@ExtendWith(SpringExtension::class, LocalstackDockerExtension::class)
@LocalstackDockerProperties(
        services = ["iam"],
        pullNewImage = false,
        imageTag = "0.12.2",
        useSingleDockerContainer = false
)
class PolicyManagerShould {
    init {
        System.setProperty("aws.accessKeyId", "0")
        System.setProperty("aws.secretAccessKey", "0")
    }

    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var policyManager: PolicyManager

    val awsCredentials: AwsBasicCredentials = AwsBasicCredentials.create("0", "0")
    val policySsmTagsJsonStr: String = ResourceHelper.readAsString("/lib/aws/iam/policySsmTags.json")

    @Test
    fun `create a new policy`() {
        val createResp = policyManager.create(awsCredentials,
            name = "SomeFancyName",
            document = policySsmTagsJsonStr
        )
        val getVersionResp = policyManager.getVersion(awsCredentials, createResp.arn, createResp.versionId)
        Assertions.assertEquals(createResp, getVersionResp)
    }

    @Test
    fun `throw an exception if policy already exists`() {
        policyManager.create(awsCredentials,
                name = "duplicateName",
                document = policySsmTagsJsonStr
        )
        Assertions.assertThrows(DuplicatePolicyException::class.java) {
            policyManager.create(awsCredentials,
                    name = "duplicateName",
                    document = policySsmTagsJsonStr
            )
        }
    }

    @Test
    fun `throw an exception if policy does not exist`() {
        Assertions.assertThrows(PolicyNotFoundException::class.java) {
            policyManager.getVersion(awsCredentials,
                    arn = "unknown_arn",
                    versionId = "unknown_version"
            )
        }
    }
}
package dev.santos.awssshservermanager.lib.aws.iam

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import dev.santos.awssshservermanager.helper.ResourceHelper
import dev.santos.awssshservermanager.helper.lib.aws.iam.AwsUserManager
import dev.santos.awssshservermanager.lib.aws.exception.DuplicatePolicyException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyAttachmentParamNotFoundException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import dev.santos.awssshservermanager.lib.aws.model.IamAttachedPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
@Import(AwsUserManager::class)
class PolicyManagerShould {
  init {
    System.setProperty("aws.accessKeyId", "0")
    System.setProperty("aws.secretAccessKey", "0")
  }

  @Autowired
  private lateinit var iamClientBuilder: IamClientBuilder

  @Autowired
  private lateinit var policyManager: PolicyManager

  @Autowired
  private lateinit var awsUserManager: AwsUserManager

  val awsCredentials: AwsBasicCredentials = AwsBasicCredentials.create("0", "0")
  val policySsmTagsJsonStr: String = ResourceHelper.readAsString("/lib/aws/iam/policySsmTags.json")

  @Test
  fun `create a new policy`() {
    val createResp = policyManager.create(
      awsCredentials,
      name = "SomeFancyName",
      document = policySsmTagsJsonStr
    )
    val getVersionResp = policyManager.getVersion(awsCredentials, createResp.arn, createResp.versionId)
    Assertions.assertEquals(createResp, getVersionResp)
  }

  @Test
  fun `throw an exception if policy already exists`() {
    policyManager.create(
      awsCredentials,
      name = "duplicateName",
      document = policySsmTagsJsonStr
    )
    Assertions.assertThrows(DuplicatePolicyException::class.java) {
      policyManager.create(
        awsCredentials,
        name = "duplicateName",
        document = policySsmTagsJsonStr
      )
    }
  }

  @Test
  fun `throw an exception if policy does not exist`() {
    Assertions.assertThrows(PolicyNotFoundException::class.java) {
      policyManager.getVersion(
        awsCredentials,
        arn = "unknown_arn",
        versionId = "unknown_version"
      )
    }
  }

  @Test
  fun `attach an new policy`() {
    val testUser = "test.user"
    val testPolicyName = "attach_new_policy_test"
    awsUserManager.create(awsCredentials, testUser)
    val createResp = policyManager.create(
      awsCredentials,
      name = testPolicyName,
      document = policySsmTagsJsonStr
    )
    policyManager.attachUserPolicy(
      awsCredentials,
      arn = createResp.arn,
      userName = testUser
    )
    assertThat(policyManager.listAttachedUserPolicies(awsCredentials, testUser))
      .contains(IamAttachedPolicy(testPolicyName, createResp.arn))
  }

  @Test
  fun `throw exception if user policy attachment entities don't exist`() {
    Assertions.assertThrows(PolicyAttachmentParamNotFoundException::class.java) {
      policyManager.attachUserPolicy(
        awsCredentials,
        arn = "invalid_arn",
        userName = "invalid_username"
      )
    }
  }

  @Test
  fun `throw exception if user policy listing entities don't exist`() {
    Assertions.assertThrows(PolicyAttachmentParamNotFoundException::class.java) {
      policyManager.listAttachedUserPolicies(
        awsCredentials,
        userName = "invalid_username"
      )
    }
  }
}

package dev.santos.awssshservermanager.lib.aws.iam

import dev.santos.awssshservermanager.IntegrationTestBase
import dev.santos.awssshservermanager.helper.ResourceHelper
import dev.santos.awssshservermanager.lib.aws.exception.DuplicatePolicyException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyAttachmentParamNotFoundException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import dev.santos.awssshservermanager.lib.aws.model.IamAttachedPolicy
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyManagerShould : IntegrationTestBase() {
  init {
    System.setProperty("aws.accessKeyId", "0")
    System.setProperty("aws.secretAccessKey", "0")
  }

  val awsCredentials: AwsBasicCredentials = AwsBasicCredentials.create("0", "0")
  val policySsmTagsJsonStr: String = ResourceHelper.readAsString("/lib/aws/iam/policySsmTags.json")

  val testPolicyName = "test_policy_name"
  var validPolicy = IamPolicy("", "", "")

  @BeforeEach
  internal fun setup() {
    validPolicy = policyManager.create(
      awsCredentials,
      name = testPolicyName,
      document = policySsmTagsJsonStr
    )
  }

  @AfterEach
  internal fun tearDown() {
    policyManager.listAttachedUserPolicies(awsCredentials, tenantUserName)
      .forEach { policyManager.detachUserPolicy(awsCredentials, it.arn, tenantUserName) }
    policyManager.remove(
      awsCredentials,
      arn = validPolicy.arn
    )
  }

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
    Assertions.assertThrows(DuplicatePolicyException::class.java) {
      policyManager.create(
        awsCredentials,
        name = testPolicyName,
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
    policyManager.attachUserPolicy(
      awsCredentials,
      arn = validPolicy.arn,
      userName = tenantUserName
    )
    assertThat(policyManager.listAttachedUserPolicies(awsCredentials, tenantUserName))
      .contains(IamAttachedPolicy(testPolicyName, validPolicy.arn))
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

  @Test
  fun `detach an new policy`() {
    policyManager.attachUserPolicy(
      awsCredentials,
      arn = validPolicy.arn,
      userName = tenantUserName
    )
    policyManager.listAttachedUserPolicies(awsCredentials, tenantUserName)
      .contains(IamAttachedPolicy(testPolicyName, validPolicy.arn))
    policyManager.detachUserPolicy(
      awsCredentials,
      arn = validPolicy.arn,
      userName = tenantUserName
    )
    assertThat(policyManager.listAttachedUserPolicies(awsCredentials, tenantUserName))
      .doesNotContain(IamAttachedPolicy(testPolicyName, validPolicy.arn))
    policyManager.attachUserPolicy(
      awsCredentials,
      arn = validPolicy.arn,
      userName = tenantUserName
    )
  }
}

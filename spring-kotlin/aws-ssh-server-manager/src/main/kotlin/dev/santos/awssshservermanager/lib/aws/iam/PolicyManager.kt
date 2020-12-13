package dev.santos.awssshservermanager.lib.aws.iam

import dev.santos.awssshservermanager.lib.aws.config.AwsIamConfig
import dev.santos.awssshservermanager.lib.aws.exception.DuplicatePolicyException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyAttachmentParamNotFoundException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import dev.santos.awssshservermanager.lib.aws.model.IamAttachedPolicy
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.services.iam.model.AttachUserPolicyRequest
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest
import software.amazon.awssdk.services.iam.model.GetPolicyVersionRequest
import software.amazon.awssdk.services.iam.model.IamException
import software.amazon.awssdk.services.iam.model.ListAttachedUserPoliciesRequest

@Component
class PolicyManager(
  @Autowired val awsIamConfig: AwsIamConfig,
  @Autowired val iamClientBuilder: IamClientBuilder
) {
  fun create(awsCredentials: AwsCredentials, name: String, document: String): IamPolicy {
    val request = CreatePolicyRequest
      .builder()
      .policyName(name)
      .policyDocument(document)
      .path("${awsIamConfig.path}/")
      .build()

    try {
      val response = iamClientBuilder.buildClient(awsCredentials).createPolicy(request)
      return IamPolicy(
        document = document,
        versionId = response.policy().defaultVersionId(),
        arn = response.policy().arn(),
      )
    } catch (e: IamException) {
      when (e.statusCode()) {
        409 -> throw DuplicatePolicyException(e.message.orEmpty())
        else -> throw e
      }
    }
  }

  fun getVersion(awsCredentials: AwsCredentials, arn: String, versionId: String): IamPolicy {
    val request = GetPolicyVersionRequest
      .builder()
      .policyArn(arn)
      .versionId(versionId)
      .build()

    try {
      val response = iamClientBuilder.buildClient(awsCredentials).getPolicyVersion(request)
      return IamPolicy(
        document = response.policyVersion().document(),
        versionId = versionId,
        arn = arn
      )
    } catch (e: IamException) {
      when (e.statusCode()) {
        404 -> throw PolicyNotFoundException(e.message.orEmpty())
        else -> throw e
      }
    }
  }

  fun attachUserPolicy(awsCredentials: AwsCredentials, arn: String, userName: String) {
    val request = AttachUserPolicyRequest
      .builder()
      .policyArn(arn)
      .userName(userName)
      .build()

    try {
      iamClientBuilder.buildClient(awsCredentials).attachUserPolicy(request)
    } catch (e: IamException) {
      when (e.statusCode()) {
        404 -> throw PolicyAttachmentParamNotFoundException(e.message.orEmpty())
        else -> throw e
      }
    }
  }

  fun listAttachedUserPolicies(awsCredentials: AwsCredentials, userName: String): List<IamAttachedPolicy> {
    val request = ListAttachedUserPoliciesRequest
      .builder()
      .pathPrefix(awsIamConfig.path)
      .userName(userName)
      .build()

    try {
      return iamClientBuilder.buildClient(awsCredentials)
        .listAttachedUserPolicies(request)
        .attachedPolicies()
        .map { IamAttachedPolicy(arn = it.policyArn(), name = it.policyName()) }
    } catch (e: IamException) {
      when (e.statusCode()) {
        404 -> throw PolicyAttachmentParamNotFoundException(e.message.orEmpty())
        else -> throw e
      }
    }
  }
}

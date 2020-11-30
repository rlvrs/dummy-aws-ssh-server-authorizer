package dev.santos.awssshservermanager.lib.aws.iam

import dev.santos.awssshservermanager.lib.aws.config.AwsIamConfig
import dev.santos.awssshservermanager.lib.aws.exception.DuplicatePolicyException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.services.iam.IamClient
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest
import software.amazon.awssdk.services.iam.model.GetPolicyVersionRequest
import software.amazon.awssdk.services.iam.model.IamException

@Component
class PolicyManager(@Autowired private val awsIamConfig: AwsIamConfig) {

    fun create(awsCredentials: AwsCredentials, name: String, document: String): IamPolicy {
        IamClient.builder()
                .region(awsIamConfig.region)
        val request = CreatePolicyRequest
                .builder()
                .policyName(name)
                .policyDocument(document)
                .path("${awsIamConfig.path}/")
                .build()

        try {
            val response = awsIamConfig.buildClient(awsCredentials).createPolicy(request)
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
        IamClient.builder()
                .region(awsIamConfig.region)
        val request = GetPolicyVersionRequest
                .builder()
                .policyArn(arn)
                .versionId(versionId)
                .build()

        try {
            val response = awsIamConfig.buildClient(awsCredentials).getPolicyVersion(request)
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
}
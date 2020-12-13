package dev.santos.awssshservermanager.helper.lib.aws.iam

import dev.santos.awssshservermanager.lib.aws.config.AwsIamConfig
import dev.santos.awssshservermanager.lib.aws.iam.IamClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.services.iam.model.CreateUserRequest

@TestComponent
class AwsUserManager(
  @Autowired val awsIamConfig: AwsIamConfig,
  @Autowired val iamClientBuilder: IamClientBuilder
) {
  fun create(awsCredentials: AwsCredentials, userName: String) {
    val request = CreateUserRequest
      .builder()
      .path("${awsIamConfig.path}/")
      .userName(userName)
      .build()

    iamClientBuilder.buildClient(awsCredentials)
      .createUser(request)
  }
}

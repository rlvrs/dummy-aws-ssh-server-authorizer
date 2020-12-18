package dev.santos.awssshservermanager.helper.lib.aws.iam

import dev.santos.awssshservermanager.lib.aws.config.AwsIamConfig
import dev.santos.awssshservermanager.lib.aws.iam.IamClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.services.iam.IamClient
import software.amazon.awssdk.services.iam.model.CreateAccessKeyRequest
import software.amazon.awssdk.services.iam.model.CreateUserRequest
import software.amazon.awssdk.services.iam.model.DeleteUserRequest
import software.amazon.awssdk.services.iam.model.GetUserRequest
import software.amazon.awssdk.services.iam.model.IamException
import software.amazon.awssdk.services.iam.model.User

data class AwsAccessKey(
  val accessKeyId: String,
  val secretAccessKey: String
)

@TestComponent
class AwsUserManager(
  @Autowired val awsIamConfig: AwsIamConfig,
  @Autowired val iamClientBuilder: IamClientBuilder
) {
  @Bean
  fun rootIamClient(): IamClient {
    val builder = IamClient
      .builder()
      .region(awsIamConfig.region)

    return when (awsIamConfig.endpointUri) {
      null -> builder
      else -> builder.endpointOverride(awsIamConfig.endpointUri)
    }.build()
  }

  fun create(userName: String) {
    val request = CreateUserRequest
      .builder()
      .path("${awsIamConfig.path}/")
      .userName(userName)
      .build()

    rootIamClient().createUser(request)
  }

  fun createAccessKey(userName: String): AwsAccessKey {
    val request = CreateAccessKeyRequest
      .builder()
      .userName(userName)
      .build()

    val response = rootIamClient()
      .createAccessKey(request)

    return AwsAccessKey(
      accessKeyId = response.accessKey().accessKeyId(),
      secretAccessKey = response.accessKey().secretAccessKey()
    )
  }

  fun create(awsCredentials: AwsCredentials, userName: String) {
    val request = CreateUserRequest
      .builder()
      .path("${awsIamConfig.path}/")
      .userName(userName)
      .build()

    iamClientBuilder.buildClient(awsCredentials)
      .createUser(request)
  }

  fun remove(awsCredentials: AwsCredentials, userName: String) {
    val request = DeleteUserRequest
      .builder()
      .userName(userName)
      .build()

    iamClientBuilder.buildClient(awsCredentials)
      .deleteUser(request)
  }

  fun get(userName: String): User {
    val request = GetUserRequest
      .builder()
      .userName(userName)
      .build()

    return rootIamClient()
      .getUser(request)
      .user()
  }

  fun exists(userName: String): Boolean {
    return try {
      this.get(userName)
      true
    } catch (exception: IamException) {
      exception.statusCode() != 404
    }
  }
}

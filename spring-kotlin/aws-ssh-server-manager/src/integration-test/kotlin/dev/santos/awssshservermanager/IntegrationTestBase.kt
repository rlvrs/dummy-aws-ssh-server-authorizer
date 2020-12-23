package dev.santos.awssshservermanager

import cloud.localstack.docker.LocalstackDockerExtension
import cloud.localstack.docker.annotation.LocalstackDockerProperties
import dev.santos.awssshservermanager.adapter.persistence.PermissionRepository
import dev.santos.awssshservermanager.application.port.input.RemoveExpiredPermissionsUseCase
import dev.santos.awssshservermanager.application.service.RemoveExpiredPermissionsJob
import dev.santos.awssshservermanager.helper.PgsqlContainer
import dev.santos.awssshservermanager.helper.lib.aws.iam.AwsAccessKey
import dev.santos.awssshservermanager.helper.lib.aws.iam.AwsUserManager
import dev.santos.awssshservermanager.hostgroup.CreateHostGroupFixture
import dev.santos.awssshservermanager.lib.aws.iam.IamClientBuilder
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.mapper.HostGroupMapper
import dev.santos.awssshservermanager.permission.CreatePermissionFixture
import dev.santos.awssshservermanager.permission.RemovePermissionFixture
import dev.santos.awssshservermanager.repository.HostGroupRepository
import dev.santos.awssshservermanager.repository.TenantRepository
import dev.santos.awssshservermanager.repository.UserRepository
import dev.santos.awssshservermanager.tenant.CreateTenantFixture
import dev.santos.awssshservermanager.user.CreateUserFixture
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension::class, LocalstackDockerExtension::class)
@LocalstackDockerProperties(
  services = ["iam"],
  pullNewImage = false,
  imageTag = "0.12.2",
  useSingleDockerContainer = true
)
@ComponentScan(basePackageClasses = [IntegrationTestBase::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class IntegrationTestBase : PgsqlContainer {
  // Not worth testing that the background jobs lib does its job
  @MockBean
  lateinit var removeExpiredPermissionsJob: RemoveExpiredPermissionsJob

  @Autowired
  lateinit var passwordEncoder: PasswordEncoder

  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var iamClientBuilder: IamClientBuilder

  @Autowired
  lateinit var policyManager: PolicyManager

  @Autowired
  lateinit var awsUserManager: AwsUserManager

  @Autowired
  lateinit var removeExpiredPermissionsUseCase: RemoveExpiredPermissionsUseCase

  @Autowired
  lateinit var tenantRepository: TenantRepository

  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var hostGroupRepository: HostGroupRepository

  @Autowired
  lateinit var permissionRepository: PermissionRepository

  @Autowired
  lateinit var hostGroupMapper: HostGroupMapper

  @Autowired
  lateinit var createTenantFixture: CreateTenantFixture

  @Autowired
  lateinit var createUserFixture: CreateUserFixture

  @Autowired
  lateinit var createHostGroupFixture: CreateHostGroupFixture

  @Autowired
  lateinit var createPermissionFixture: CreatePermissionFixture

  @Autowired
  lateinit var removePermissionFixture: RemovePermissionFixture

  val tenantUserName: String = "test.user"

  companion object {
    // Shared across all test classes
    lateinit var tenantAwsAccessKey: AwsAccessKey
  }

  @BeforeAll
  fun createTenantTestUser() {
    if (!awsUserManager.exists(tenantUserName)) {
      awsUserManager.create(tenantUserName)
      tenantAwsAccessKey = awsUserManager.createAccessKey(tenantUserName)
    }
  }
}

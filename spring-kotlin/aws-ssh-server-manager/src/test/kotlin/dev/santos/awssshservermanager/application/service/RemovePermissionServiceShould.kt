package dev.santos.awssshservermanager.application.service

import UnitTestBase
import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand
import dev.santos.awssshservermanager.application.port.output.GetAwsCredentialsPort
import dev.santos.awssshservermanager.application.port.output.GetHostGroupPort
import dev.santos.awssshservermanager.application.port.output.RemovePermissionPort
import dev.santos.awssshservermanager.exception.HostGroupNotFoundException
import dev.santos.awssshservermanager.exception.HostGroupTenantNotFoundException
import dev.santos.awssshservermanager.exception.PermissionNotFoundException
import dev.santos.awssshservermanager.exception.PermissionTenantNotFoundException
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.model.GranteeType
import dev.santos.awssshservermanager.model.HostGroup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

@SpringBootTest
@ComponentScan(basePackageClasses = [RemovePermissionServiceShould::class])
class RemovePermissionServiceShould : UnitTestBase() {
  @Mock
  private lateinit var policyManager: PolicyManager

  @Mock
  private lateinit var getAwsCredentialsPort: GetAwsCredentialsPort

  @Mock
  private lateinit var getHostGroupPort: GetHostGroupPort

  @Mock
  private lateinit var removePermissionPort: RemovePermissionPort

  @InjectMocks
  private lateinit var removePermissionService: RemovePermissionService

  private val validCommand = RemovePermissionCommand(
    id = 1L,
    tenantId = 2L,
    hostGroupId = 3L,
    grantee = "test.user",
    granteeType = GranteeType.USER,
  )

  @Test
  fun `remove a permission successfully`() {
    BDDMockito
      .given(getHostGroupPort.getHostGroup(validCommand.tenantId, validCommand.hostGroupId))
      .willReturn(HostGroup(matchers = listOf()))
    BDDMockito
      .doNothing()
      .`when`(removePermissionPort).removePermission(validCommand.tenantId, validCommand.id)

    Assertions.assertEquals(removePermissionService.removePermission(validCommand), 1L)
  }

  @TestFactory
  fun `throw an exception upon a constraint violation`() = listOf(
    Pair(
      "permission tenant not found",
      PermissionTenantNotFoundException("Tenant -1 not found!")
    ),
    Pair(
      "host group tenant not found",
      HostGroupTenantNotFoundException("Tenant -1 not found!")
    ),
    Pair(
      "permission host group not found",
      HostGroupNotFoundException("Host group -1 not found!")
    ),
    Pair(
      "permission policy not found",
      PermissionNotFoundException("No such entity: policyArn or userName")
    ),
  ).map { (testName: String, exception: Exception) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito
        .doThrow(exception)
        .`when`(removePermissionPort).removePermission(validCommand.tenantId, validCommand.id)

      BDDMockito
        .given(getHostGroupPort.getHostGroup(validCommand.tenantId, validCommand.hostGroupId))
        .willReturn(HostGroup(matchers = listOf()))

      Assertions.assertThrows(exception.javaClass) {
        removePermissionService.removePermission(validCommand)
      }
    }
  }
}

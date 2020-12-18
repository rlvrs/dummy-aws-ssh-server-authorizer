package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.adapter.persistence.PermissionRepository
import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.exception.DuplicatePermissionException
import dev.santos.awssshservermanager.exception.PermissionGrantorNotFoundException
import dev.santos.awssshservermanager.exception.PermissionHostGroupNotFoundException
import dev.santos.awssshservermanager.exception.PermissionTenantNotFoundException
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.mapper.PermissionMapperImpl
import dev.santos.awssshservermanager.model.HostGroup
import dev.santos.awssshservermanager.model.Permission
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Spy
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.sql.SQLException
import java.util.concurrent.TimeUnit

@SpringBootTest
@ExtendWith(SpringExtension::class)
class PermissionServiceShould {
  @Mock
  private lateinit var policyManager: PolicyManager

  @Mock
  private lateinit var tenantService: TenantService

  @Mock
  private lateinit var hostGroupService: HostGroupService

  @Mock
  private lateinit var permissionRepository: PermissionRepository

  @Spy
  private lateinit var permissionMapper: PermissionMapperImpl

  @InjectMocks
  private lateinit var permissionService: PermissionService

  private val validCreatePermissionDto = CreatePermissionDto(
    tenantId = 1L,
    grantorId = 1L,
    hostGroupId = 1L,
    grantee = "grantee.username",
    granteeType = "USER",
    expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
  )

  val expectedPermission = Permission(
    id = 2L,
    tenantId = 1L
  )

  @Test
  fun `create a permission successfully`() {
    given(hostGroupService.getHostGroupById(anyLong()))
      .willReturn(HostGroup(matchers = listOf()))
    given(permissionRepository.save(any(Permission::class.java)))
      .willReturn(expectedPermission)

    Assertions.assertEquals(permissionService.create(validCreatePermissionDto), 2L)
  }

  data class DbConstraintViolationInput<T>(
    val exceptionMessage: String,
    val constraintName: String,
    val exceptionClass: Class<T>
  )

  @TestFactory
  fun `throw an exception upon DB constraint violation`() = listOf(
    Pair(
      "duplicate permission",
      DbConstraintViolationInput(
        "Permission -1 already exists!",
        "unique_tenantid_grantee_hostgroup",
        DuplicatePermissionException::class.java
      )
    ),
    Pair(
      "permission tenant not found",
      DbConstraintViolationInput(
        "Tenant -1 not found!",
        "fk_tenant_id",
        PermissionTenantNotFoundException::class.java
      )
    ),
    Pair(
      "permission grantor not found",
      DbConstraintViolationInput(
        "GrantorId -1 not found!",
        "fk_grantor_id",
        PermissionGrantorNotFoundException::class.java
      )
    ),
    Pair(
      "permission host group not found",
      DbConstraintViolationInput(
        "HostGroupId -1 not found!",
        "fk_host_group_id",
        PermissionHostGroupNotFoundException::class.java
      )
    ),
  ).map { (testName: String, input: DbConstraintViolationInput<out RuntimeException>) ->
    DynamicTest.dynamicTest(testName) {
      doThrow(
        DataIntegrityViolationException(
          input.exceptionMessage,
          ConstraintViolationException("", SQLException(), input.constraintName)
        )
      ).`when`(permissionRepository).save(any())

      given(hostGroupService.getHostGroupById(anyLong()))
        .willReturn(HostGroup(matchers = listOf()))

      Assertions.assertThrows(input.exceptionClass) {
        permissionService.create(validCreatePermissionDto)
      }
    }
  }
}

package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.IntegrationTestBase
import dev.santos.awssshservermanager.adapter.web.getvalidpermissions.GetValidPermissionResponse
import dev.santos.awssshservermanager.application.port.input.GetValidPermissionsUseCase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired

@TestMethodOrder(
  MethodOrderer.OrderAnnotation::class
)
class GetPermissionsIntegrationTest : IntegrationTestBase() {
  @Autowired
  lateinit var getValidPermissionsUseCase: GetValidPermissionsUseCase

  @Test
  @Order(1)
  fun `given valid permissions`() {
    createTenantFixture.createTenant(tenantAwsAccessKey)
    createUserFixture.createUser()
    createHostGroupFixture.createHostGroup()
    createPermissionFixture.createPermission()

    Assertions.assertThat(permissionRepository.count()).isEqualTo(1)
  }

  @Test
  @Order(2)
  fun `gets all valid permissions successfully`() {
    val validPermissions: List<GetValidPermissionResponse> = getValidPermissionsUseCase
      .getValidPermissions(1L)

    Assertions.assertThat(validPermissions)
      .hasSize(1)
    Assertions.assertThat(validPermissions[0].hostGroup.id)
      .isEqualTo(createPermissionFixture.createPermissionDto.hostGroupId)
    Assertions.assertThat(validPermissions[0].grantee)
      .isEqualTo(createPermissionFixture.createPermissionDto.grantee)
    Assertions.assertThat(validPermissions[0].granteeType.name)
      .isEqualTo(createPermissionFixture.createPermissionDto.granteeType)
    Assertions.assertThat(validPermissions[0].grantor.id)
      .isEqualTo(createPermissionFixture.createPermissionDto.grantorId)
    Assertions.assertThat(validPermissions[0].tenantId)
      .isEqualTo(createPermissionFixture.createPermissionDto.tenantId)
  }
}

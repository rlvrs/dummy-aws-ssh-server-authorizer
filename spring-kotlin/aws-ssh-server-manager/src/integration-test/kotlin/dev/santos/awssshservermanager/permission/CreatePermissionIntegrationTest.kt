package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.IntegrationTestBase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(
  MethodOrderer.OrderAnnotation::class
)
class CreatePermissionIntegrationTest : IntegrationTestBase() {
  @Test
  @Order(1)
  fun `creates permission successfully`() {
    createTenantFixture.createTenant(tenantAwsAccessKey)
    createUserFixture.createUser()
    createHostGroupFixture.createHostGroup()
    createPermissionFixture.createPermission()

    val newPermission = permissionRepository.findById(1L).get()
    Assertions.assertThat(newPermission.id).isEqualTo(1L)
    Assertions.assertThat(newPermission.tenantId).isEqualTo(createPermissionFixture.createPermissionDto.tenantId)
    Assertions.assertThat(newPermission.hostGroup.id).isEqualTo(createPermissionFixture.createPermissionDto.hostGroupId)
    Assertions.assertThat(newPermission.grantor.id).isEqualTo(createPermissionFixture.createPermissionDto.grantorId)
    Assertions.assertThat(newPermission.granteeType.name)
      .isEqualTo(createPermissionFixture.createPermissionDto.granteeType)
    Assertions.assertThat(newPermission.grantee).isEqualTo(createPermissionFixture.createPermissionDto.grantee)
    Assertions.assertThat(newPermission.expirationTimeMinutes)
      .isEqualTo(createPermissionFixture.createPermissionDto.expirationTimeMinutes)
  }

  @Test
  @Order(2)
  fun `removes permission successfully`() {
    val oldNumPermissions = permissionRepository.count()

    removePermissionFixture.removePermission()

    val currNumPermissions = permissionRepository.count()

    Assertions.assertThat(oldNumPermissions)
      .isEqualTo(currNumPermissions + 1)
  }
}

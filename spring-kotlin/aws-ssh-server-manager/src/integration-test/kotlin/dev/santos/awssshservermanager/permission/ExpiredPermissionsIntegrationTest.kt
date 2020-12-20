package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.IntegrationTestBase
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ExpiredPermissionsIntegrationTest : IntegrationTestBase() {
  @Test
  fun `removes expired permissions successfully`() {
    permissionRepository.deleteAll()

    createTenantFixture.createTenant(tenantAwsAccessKey)
    createUserFixture.createUser()
    createHostGroupFixture.createHostGroup()
    createPermissionFixture.createExpiredPermission()

    Assertions.assertThat(permissionRepository.count())
      .isEqualTo(1L)

    removeExpiredPermissionsUseCase.removeExpiredPermissions()

    await
      .atMost(5, TimeUnit.SECONDS).until {
        permissionRepository.count() == 0L
      }
  }
}

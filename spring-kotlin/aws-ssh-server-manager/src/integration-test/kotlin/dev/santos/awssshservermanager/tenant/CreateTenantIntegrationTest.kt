package dev.santos.awssshservermanager.tenant

import dev.santos.awssshservermanager.IntegrationTestBase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CreateTenantIntegrationTest : IntegrationTestBase() {
  @Test
  fun `creates tenant successfully`() {
    val expectedId = 1L

    createTenantFixture.createTenant(tenantAwsAccessKey)

    val newTenant = tenantRepository.findById(expectedId).get()
    Assertions.assertThat(newTenant.awsApiKey).isEqualTo(tenantAwsAccessKey.accessKeyId)
    Assertions.assertThat(newTenant.awsApiSecret).isEqualTo(tenantAwsAccessKey.secretAccessKey)
    Assertions.assertThat(newTenant.id).isEqualTo(expectedId)
    Assertions.assertThat(newTenant.name).isEqualTo(createTenantFixture.tenantName)
  }
}

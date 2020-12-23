package dev.santos.awssshservermanager.mapper

import UnitTestBase
import dev.santos.awssshservermanager.dto.CreateTenantRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TenantMapperShould : UnitTestBase() {
  @Autowired
  lateinit var tenantMapper: TenantMapper

  @TestFactory
  fun `map to Tenant valid CreateTenantRequest`() = listOf(
    Pair(
      "create tenant request with all fields",
      CreateTenantRequest(
        name = "some-company",
        awsApiKey = "super_secret_key",
        awsApiSecret = "super_secret_secret"
      )
    ),
    Pair(
      "create tenant request with name and key",
      CreateTenantRequest(
        name = "some-company",
        awsApiKey = "super_secret_key",
        awsApiSecret = "",
      )
    ),
  ).map { (testName: String, createTenantRequest: CreateTenantRequest) ->
    DynamicTest.dynamicTest(testName) {
      val mappedTenant = tenantMapper.toTenant(createTenantRequest)

      assertThat(mappedTenant).isNotNull
      assertThat(mappedTenant.name).isEqualTo(createTenantRequest.name)
      assertThat(mappedTenant.awsApiKey).isEqualTo(createTenantRequest.awsApiKey)
      assertThat(mappedTenant.awsApiSecret).isEqualTo(createTenantRequest.awsApiSecret)
    }
  }
}

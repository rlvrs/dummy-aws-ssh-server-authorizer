package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateTenantDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class TenantMapperShould {
  @Autowired
  lateinit var tenantMapper: TenantMapper

  @TestFactory
  fun `map to Tenant valid CreateTenantDto`() = listOf(
    Pair(
      "create tenant dto with all fields",
      CreateTenantDto(
        name = "some-company",
        awsApiKey = "super_secret_key",
        awsApiSecret = "super_secret_secret"
      )
    ),
    Pair(
      "create tenant dto with name and key",
      CreateTenantDto(
        name = "some-company",
        awsApiKey = "super_secret_key"
      )
    ),
  ).map { (testName: String, createTenantDto: CreateTenantDto) ->
    DynamicTest.dynamicTest(testName) {
      val mappedTenant = tenantMapper.toTenant(createTenantDto)

      assertThat(mappedTenant).isNotNull
      assertThat(mappedTenant.name).isEqualTo(createTenantDto.name)
      assertThat(mappedTenant.awsApiKey).isEqualTo(createTenantDto.awsApiKey)
      assertThat(mappedTenant.awsApiSecret).isEqualTo(createTenantDto.awsApiSecret)
    }
  }
}

package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.mapper.TenantMapperImpl
import dev.santos.awssshservermanager.model.Tenant
import dev.santos.awssshservermanager.repository.TenantRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Spy
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@Import(TenantMapperImpl::class)
class TenantServiceShould {
  @Mock
  private lateinit var tenantRepository: TenantRepository

  @Spy
  private lateinit var tenantMapper: TenantMapperImpl

  @InjectMocks
  private lateinit var tenantService: TenantService

  @Test
  fun `create a tenant successfully`() {
    val inputCreateTenantRequest = CreateTenantRequest(
      name = "some-company",
      awsApiKey = "super_secret_key",
      awsApiSecret = "super_secret_secret"
    )
    val expectedId: Long = 1
    val expectedTenant = Tenant(
      id = expectedId
    )

    given(tenantRepository.save(any(Tenant::class.java)))
      .willReturn(expectedTenant)

    Assertions.assertEquals(tenantService.create(inputCreateTenantRequest), expectedId)
  }

  @Test
  fun `throw an exception when the tenant exists in the DB`() {
    val inputCreateTenantRequest = CreateTenantRequest(
      name = "some-company",
      awsApiKey = "super_secret_key",
      awsApiSecret = "super_secret_secret"
    )

    doThrow(DataIntegrityViolationException("Tenant -1 already exists!"))
      .`when`(tenantRepository).save(any())

    Assertions.assertThrows(DuplicateTenantException::class.java) {
      tenantService.create(inputCreateTenantRequest)
    }
  }
}

package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantDto
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.helper.safeEq
import dev.santos.awssshservermanager.lib.aws.exception.DuplicateSecretException
import dev.santos.awssshservermanager.lib.aws.ssm.SecretsManager
import dev.santos.awssshservermanager.model.Tenant
import dev.santos.awssshservermanager.repository.TenantRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
class TenantServiceShould {
    @Mock
    private lateinit var tenantRepository: TenantRepository

    @Mock
    private lateinit var secretsManager: SecretsManager

    @InjectMocks
    private lateinit var tenantService: TenantService

    @Test
    fun `return the new tenant id when successful`() {
        val inputDto = CreateTenantDto(
            name = "some-company",
            awsApiKey = "super_secret_key",
            awsApiSecret = "super_secret_secret"
        )
        val expectedId: Long = 1
        val expectedKeyName = "key_name"
        val expectedSecretName = "secret_name"
        val expectedTenant = Tenant(
            id = expectedId,
            name = inputDto.toTenant().name,
            awsApiKeySsmName = expectedKeyName,
            awsApiSecretSsmName = expectedSecretName
        )

        given(secretsManager.saveSecret(safeEq("some-company-api-key"), anyString()))
                .willReturn(expectedKeyName)
        given(secretsManager.saveSecret(safeEq(inputDto.toTenant().awsApiSecretSsmName), anyString()))
                .willReturn(expectedSecretName)
        given(tenantRepository.save(any(Tenant::class.java)))
            .willReturn(expectedTenant)

        Assertions.assertEquals(tenantService.create(inputDto), expectedId)
    }

    @Test
    fun `throw exception when the tenant exists in SSM`() {
        val inputDto = CreateTenantDto(
            name = "some-company",
            awsApiKey = "super_secret_key",
            awsApiSecret = "super_secret_secret"
        )

        given(secretsManager.saveSecret(anyString(), anyString()))
                .willAnswer{
                    throw DuplicateSecretException("Tenant -1 already exists!")
                }

        Assertions.assertThrows(DuplicateTenantException::class.java) {
            tenantService.create(inputDto)
        }
    }

    @Test
    fun `throw exception when the tenant exists in the DB`() {
        val inputDto = CreateTenantDto(
                name = "some-company",
                awsApiKey = "super_secret_key",
                awsApiSecret = "super_secret_secret"
        )

        given(secretsManager.saveSecret(safeEq("some-company-api-key"), anyString()))
                .willReturn("key_name")
        given(secretsManager.saveSecret(safeEq(inputDto.toTenant().awsApiSecretSsmName), anyString()))
                .willReturn("secret_name")
        doThrow(DataIntegrityViolationException("Tenant -1 already exists!"))
                .`when`(tenantRepository).save(any(Tenant::class.java))

        Assertions.assertThrows(DuplicateTenantException::class.java) {
            tenantService.create(inputDto)
        }
    }
}
package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantDto
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.exception.TenantNotFoundException
import dev.santos.awssshservermanager.lib.aws.exception.DuplicateSecretException
import dev.santos.awssshservermanager.lib.aws.ssm.SecretsManager
import dev.santos.awssshservermanager.model.Tenant
import dev.santos.awssshservermanager.repository.TenantRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials

@Service
class TenantService(val tenantRepository: TenantRepository, val secretsManager: SecretsManager) {
    fun create(createTenantDto: CreateTenantDto): Long {
        val newTenant = createTenantDto.toTenant()

        try {
            val prefixedKey    = secretsManager.saveSecret(newTenant.awsApiKeySsmName, createTenantDto.awsApiKey)
            val prefixedSecret = secretsManager.saveSecret(newTenant.awsApiSecretSsmName, createTenantDto.awsApiSecret)

            return tenantRepository.save(Tenant(
                name = createTenantDto.name,
                awsApiKeySsmName = prefixedKey,
                awsApiSecretSsmName = prefixedSecret,
            )).id
        } catch (exception: Exception) {
            when(exception) {
                is DataIntegrityViolationException,
                is DuplicateSecretException -> {
                    throw DuplicateTenantException(exception.message.orEmpty())
                }
                else -> throw exception
            }
        }
    }

    fun getCredentials(tenantId: Long): AwsCredentials {
        return tenantRepository.findById(tenantId)
                .map {
                    val apiKey = secretsManager.getSecret(it.awsApiKeySsmName)
                    val apiSecret = secretsManager.getSecret(it.awsApiSecretSsmName)

                    return@map AwsBasicCredentials.create(apiKey, apiSecret)
                }.orElseThrow {
                    TenantNotFoundException("Tenant id [${tenantId}] not found!")
                }
    }
}
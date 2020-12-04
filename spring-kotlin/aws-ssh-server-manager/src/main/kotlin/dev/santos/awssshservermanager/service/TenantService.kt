package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantDto
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.exception.TenantNotFoundException
import dev.santos.awssshservermanager.mapper.TenantMapper
import dev.santos.awssshservermanager.repository.TenantRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials

@Service
class TenantService(
  val tenantRepository: TenantRepository,
  val tenantMapper: TenantMapper
) {
  fun create(createTenantDto: CreateTenantDto): Long {
    try {
      val newTenant = tenantMapper.toTenant(createTenantDto)
      return tenantRepository
        .save(newTenant)
        .id
    } catch (exception: Exception) {
      when (exception) {
        is DataIntegrityViolationException -> {
          throw DuplicateTenantException(exception.message.orEmpty())
        }
        else -> throw exception
      }
    }
  }

  fun getCredentials(tenantId: Long): AwsCredentials {
    return tenantRepository.findById(tenantId)
      .map {
        return@map AwsBasicCredentials.create(it.awsApiKey, it.awsApiSecret)
      }.orElseThrow {
        TenantNotFoundException("Tenant id [${tenantId}] not found!")
      }
  }
}

package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantRequest
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
  @Throws(DuplicateTenantException::class)
  fun create(createTenantRequest: CreateTenantRequest): Long {
    try {
      val newTenant = tenantMapper.toTenant(createTenantRequest)
      return tenantRepository
        .save(newTenant)
        .id
    } catch (exception: DataIntegrityViolationException) {
      throw DuplicateTenantException(exception.message.orEmpty())
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

package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.mapper.TenantMapper
import dev.santos.awssshservermanager.repository.TenantRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

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
}

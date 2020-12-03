package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateTenantDto
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
}

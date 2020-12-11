package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.model.Tenant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface TenantMapper {
  fun toTenant(createTenantRequest: CreateTenantRequest): Tenant
}

package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateTenantDto
import dev.santos.awssshservermanager.model.Tenant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface TenantMapper {
  @Mappings(
    Mapping(source = "name", target = "name"),
    Mapping(source = "awsApiKey", target = "awsApiKey"),
    Mapping(source = "awsApiSecret", target = "awsApiSecret")
  )
  fun toTenant(createTenantDto: CreateTenantDto): Tenant
}

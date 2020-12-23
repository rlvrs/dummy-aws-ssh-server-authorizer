package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.model.Permission
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
interface PermissionMapper {
  @Mappings(
    Mapping(source = "hostGroupId", target = "hostGroup.id"),
    Mapping(source = "grantorId", target = "grantor.id")
  )
  fun toPermission(createPermissionDto: CreatePermissionDto): Permission
}

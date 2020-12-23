package dev.santos.awssshservermanager.adapter.web.getvalidpermissions

import dev.santos.awssshservermanager.model.Permission
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface GetValidPermissionsMapper {
  fun toGetValidPermissionsResponse(permission: Permission): GetValidPermissionResponse
}

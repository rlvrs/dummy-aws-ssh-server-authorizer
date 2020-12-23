package dev.santos.awssshservermanager.adapter.web.removepermission

import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface RemovePermissionMapper {
  fun toRemovePermissionCommand(removePermissionRequest: RemovePermissionRequest): RemovePermissionCommand
}

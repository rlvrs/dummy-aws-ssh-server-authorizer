package dev.santos.awssshservermanager.application.service

import dev.santos.awssshservermanager.application.port.input.RemoveExpiredPermissionsUseCase
import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand
import dev.santos.awssshservermanager.application.port.output.ExpiredPermissionEvent
import dev.santos.awssshservermanager.application.port.output.GetExpiredPermissionsPort
import dev.santos.awssshservermanager.application.port.output.PublishExpiredPermissionPort
import org.springframework.stereotype.Service

@Service
class RemoveExpiredPermissionsService(
  val getExpiredPermissionsPort: GetExpiredPermissionsPort,
  val publishExpiredPermissionPort: PublishExpiredPermissionPort
) : RemoveExpiredPermissionsUseCase {
  override fun removeExpiredPermissions() {
    getExpiredPermissionsPort.getExpiredPermissions()
      .map {
        RemovePermissionCommand(
          id = it.id,
          tenantId = it.tenantId,
          hostGroupId = it.hostGroup.id,
          grantee = it.grantee,
          granteeType = it.granteeType
        )
      }
      .map { ExpiredPermissionEvent(it) }
      .forEach { publishExpiredPermissionPort.publish(it) }
  }
}

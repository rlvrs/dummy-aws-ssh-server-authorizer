package dev.santos.awssshservermanager.adapter.persistence

import dev.santos.awssshservermanager.application.port.output.GetExpiredPermissionsPort
import dev.santos.awssshservermanager.application.port.output.GetValidPermissionsPort
import dev.santos.awssshservermanager.application.port.output.RemovePermissionPort
import dev.santos.awssshservermanager.model.Permission
import org.springframework.stereotype.Component

@Component
class PermissionPersistenceAdapter(
  private val permissionRepository: PermissionRepository
) : RemovePermissionPort, GetExpiredPermissionsPort, GetValidPermissionsPort {
  override fun removePermission(tenantId: Long, permissionId: Long) {
    permissionRepository.deleteByTenantAndId(tenantId, permissionId)
  }

  override fun getExpiredPermissions(): List<Permission> {
    return permissionRepository.findAllExpired()
  }

  override fun getValidPermissions(tenantId: Long): List<Permission> {
    return permissionRepository.findValidPermissions(tenantId)
  }
}

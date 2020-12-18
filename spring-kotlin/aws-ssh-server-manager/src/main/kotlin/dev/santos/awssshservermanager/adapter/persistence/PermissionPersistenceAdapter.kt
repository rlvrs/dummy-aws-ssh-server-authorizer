package dev.santos.awssshservermanager.adapter.persistence

import dev.santos.awssshservermanager.application.port.output.RemovePermissionPort
import org.springframework.stereotype.Component

@Component
class PermissionPersistenceAdapter(
  private val permissionRepository: PermissionRepository
) : RemovePermissionPort {
  override fun removePermission(tenantId: Long, permissionId: Long) {
    permissionRepository.deleteByTenantAndId(tenantId, permissionId)
  }
}

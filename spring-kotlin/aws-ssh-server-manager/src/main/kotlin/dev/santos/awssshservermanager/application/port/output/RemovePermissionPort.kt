package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.exception.PermissionNotFoundException

interface RemovePermissionPort {
  @Throws(PermissionNotFoundException::class)
  fun removePermission(tenantId: Long, permissionId: Long)
}

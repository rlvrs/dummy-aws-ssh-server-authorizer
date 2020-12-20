package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.model.Permission

interface GetExpiredPermissionsPort {
  fun getExpiredPermissions(): List<Permission>
}

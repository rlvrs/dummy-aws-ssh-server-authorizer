package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.model.Permission

interface GetValidPermissionsPort {
  fun getValidPermissions(tenantId: Long): List<Permission>
}

package dev.santos.awssshservermanager.application.port.input

import dev.santos.awssshservermanager.adapter.web.getvalidpermissions.GetValidPermissionResponse

interface GetValidPermissionsUseCase {
  fun getValidPermissions(tenantId: Long): List<GetValidPermissionResponse>
}

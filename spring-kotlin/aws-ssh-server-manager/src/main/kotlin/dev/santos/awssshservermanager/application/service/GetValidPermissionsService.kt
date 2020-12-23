package dev.santos.awssshservermanager.application.service

import dev.santos.awssshservermanager.adapter.web.getvalidpermissions.GetValidPermissionsMapper
import dev.santos.awssshservermanager.adapter.web.getvalidpermissions.GetValidPermissionResponse
import dev.santos.awssshservermanager.application.port.input.GetValidPermissionsUseCase
import dev.santos.awssshservermanager.application.port.output.GetValidPermissionsPort
import org.springframework.stereotype.Service

@Service
class GetValidPermissionsService(
  val getValidPermissionsPort: GetValidPermissionsPort,
  val getValidPermissionsMapper: GetValidPermissionsMapper
) : GetValidPermissionsUseCase {
  override fun getValidPermissions(tenantId: Long): List<GetValidPermissionResponse> {
    return getValidPermissionsPort.getValidPermissions(tenantId)
      .map { getValidPermissionsMapper.toGetValidPermissionsResponse(it) }
  }
}

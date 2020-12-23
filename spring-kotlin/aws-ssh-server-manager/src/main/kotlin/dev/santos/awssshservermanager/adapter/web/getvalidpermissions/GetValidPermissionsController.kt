package dev.santos.awssshservermanager.adapter.web.getvalidpermissions

import dev.santos.awssshservermanager.application.port.input.GetValidPermissionsUseCase
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class GetValidPermissionsController(
  val getValidPermissionsUseCase: GetValidPermissionsUseCase
) {
  @GetMapping("valid-permissions")
  fun removePermission(@RequestParam tenantId: Long): ResponseEntity<List<GetValidPermissionResponse>> {
    val validPermissions = getValidPermissionsUseCase.getValidPermissions(tenantId)
    return ResponseEntity.ok(validPermissions)
  }
}

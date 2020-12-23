package dev.santos.awssshservermanager.adapter.web.removepermission

import dev.santos.awssshservermanager.application.port.input.RemovePermissionUseCase
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

@Controller
class RemovePermissionController(
  val removePermissionUseCase: RemovePermissionUseCase,
  val removePermissionMapper: RemovePermissionMapper
) {
  @DeleteMapping("/permission")
  fun removePermission(@Valid @RequestBody removePermissionRequest: RemovePermissionRequest): ResponseEntity<RemovePermissionResponse> {
    val command = removePermissionMapper.toRemovePermissionCommand(removePermissionRequest)
    val removedPermissionId = removePermissionUseCase.removePermission(command)
    return ResponseEntity.ok(RemovePermissionResponse(removedPermissionId))
  }
}

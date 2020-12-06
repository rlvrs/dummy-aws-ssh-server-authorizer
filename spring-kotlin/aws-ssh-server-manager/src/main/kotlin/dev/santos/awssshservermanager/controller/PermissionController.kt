package dev.santos.awssshservermanager.controller

import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.service.PermissionService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI
import javax.validation.Valid

@Controller
class PermissionController(val permissionService: PermissionService) {
  @PostMapping("/permission")
  fun createPermission(@Valid @RequestBody createPermissionDto: CreatePermissionDto): ResponseEntity<String> {
    val newPermissionId = permissionService.create(createPermissionDto)
    val location: URI = URI.create("/permission/${newPermissionId}")
    return ResponseEntity.created(location).body("{\"id\":${newPermissionId}}")
  }
}

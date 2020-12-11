package dev.santos.awssshservermanager.controller

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.dto.CreateTenantResponse
import dev.santos.awssshservermanager.service.TenantService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI
import javax.validation.Valid

@Controller
class TenantController(val tenantService: TenantService) {
  @PostMapping("/tenant")
  fun createTenant(@Valid @RequestBody createTenantRequest: CreateTenantRequest): ResponseEntity<CreateTenantResponse> {
    val newTenantId = tenantService.create(createTenantRequest)
    val location: URI = URI.create("/tenant/${createTenantRequest.name}")
    return ResponseEntity.created(location).body(CreateTenantResponse(newTenantId))
  }
}

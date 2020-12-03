package dev.santos.awssshservermanager.controller

import dev.santos.awssshservermanager.dto.CreateTenantDto
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
  fun createTenant(@Valid @RequestBody createTenantDto: CreateTenantDto): ResponseEntity<String> {
    val newTenantId = tenantService.create(createTenantDto)
    val location: URI = URI.create("/tenant/${createTenantDto.name}")
    return ResponseEntity.created(location).body("{\"id\":${newTenantId}}")
  }
}

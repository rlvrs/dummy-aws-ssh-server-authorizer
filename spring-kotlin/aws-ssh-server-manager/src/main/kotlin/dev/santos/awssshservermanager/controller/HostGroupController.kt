package dev.santos.awssshservermanager.controller

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.service.HostGroupService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI
import javax.validation.Valid

@Controller
class HostGroupController(val hostGroupService: HostGroupService) {
    @PostMapping("/hostgroup")
    fun createHostGroup(@Valid @RequestBody hostGroupDto: CreateHostGroupDto): ResponseEntity<String> {
        val newHostGroupId = hostGroupService.create(hostGroupDto)
        val location: URI = URI.create("/hostgroup/${hostGroupDto.name}")
        return ResponseEntity.created(location).body("{\"id\":${newHostGroupId}}")
    }
}

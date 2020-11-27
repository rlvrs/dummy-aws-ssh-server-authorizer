package dev.santos.awssshservermanager.controller

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI
import javax.validation.Valid

@Controller
class UserController(val userService: UserService) {
    @PostMapping("/user")
    fun createUser(@Valid @RequestBody createUserDto: CreateUserDto): ResponseEntity<String> {
        val newUserId = userService.create(createUserDto)
        val location: URI = URI.create("/user/${createUserDto.awsUsername}")
        return ResponseEntity.created(location).body("{\"id\":${newUserId}}")
    }
}

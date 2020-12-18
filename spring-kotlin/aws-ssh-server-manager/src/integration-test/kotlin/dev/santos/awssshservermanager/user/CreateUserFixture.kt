package dev.santos.awssshservermanager.user

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.CreateUserDtoRole
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.repository.UserRepository
import org.springframework.boot.test.context.TestComponent
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@TestComponent
class CreateUserFixture(
  val userRepository: UserRepository,
  val mockMvc: MockMvc
) {
  val createUserDto = CreateUserDto(
    awsUsername = "john.doe",
    firstName = "John",
    lastName = "Doe",
    password = "str0ngP455!",
    tenantId = 1L,
    role = CreateUserDtoRole.ADMIN
  )

  fun createUser() {
    if (userRepository.findByTenantAndAwsUsername(createUserDto.tenantId, createUserDto.awsUsername).isPresent) {
      return
    }

    mockMvc.post("/user") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(createUserDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":1}") }
    }
  }
}

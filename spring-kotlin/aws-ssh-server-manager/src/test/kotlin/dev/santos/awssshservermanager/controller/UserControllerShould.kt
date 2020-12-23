package dev.santos.awssshservermanager.controller

import UnitTestBase
import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.CreateUserDtoRole
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.exception.UserTenantNotFoundException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.UserService
import org.hamcrest.Matchers
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(controllers = [UserController::class])
class UserControllerShould : UnitTestBase() {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var userService: UserService

  private val validUserDto = CreateUserDto(
    awsUsername = "john.doe",
    firstName = "John",
    lastName = "Doe",
    password = "str0ngP455!",
    tenantId = 1L,
    role = CreateUserDtoRole.ADMIN
  )
  private val blankAwsUsernameCreateUserDto = validUserDto.copy(awsUsername = "")
  private val blankFirstNameCreateUserDto = validUserDto.copy(firstName = "")
  private val blankLastNameCreateUserDto = validUserDto.copy(lastName = "")
  private val blankPasswordCreateUserDto = validUserDto.copy(password = "")
  private val smallPasswordCreateUserDto = validUserDto.copy(password = "sM4l!")
  private val bigPasswordCreateUserDto = validUserDto.copy(password = "sM4l!".repeat(51))
  private val noCapitalPasswordCreateUserDto = validUserDto.copy(password = "nocapitals3cr3t!")
  private val noLowercasePasswordCreateUserDto = validUserDto.copy(password = "254673/$#3T!")
  private val noNumbersPasswordCreateUserDto = validUserDto.copy(password = "noNumbersSecret!")
  private val noSymbolsPasswordCreateUserDto = validUserDto.copy(password = "noSymb015Secret")

  @Test
  fun `return HTTP 201 when the user is created`() {
    BDDMockito.given(this.userService.create(validUserDto))
      .willReturn(2)

    mockMvc.post("/user") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validUserDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":2}") }
      content { jsonPath<Int>("$.id", Matchers.`is`(2)) }
    }
  }

  @Test
  fun `return 409 when the user exists`() {
    BDDMockito.given(this.userService.create(validUserDto))
      .willAnswer {
        throw DuplicateUserException("User Id -1 already exists!")
      }

    mockMvc.post("/user") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validUserDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isConflict() }
    }
  }

  @Test
  fun `return 404 when the user tenant does not exist`() {
    BDDMockito.given(this.userService.create(validUserDto))
      .willAnswer {
        throw UserTenantNotFoundException("Tenant Id -1 does not exist.")
      }

    mockMvc.post("/user") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validUserDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isNotFound() }
    }
  }

  @TestFactory
  fun `return 400 when the payload is invalid`() = listOf(
    Pair("blank AWS username", objToJsonStr(blankAwsUsernameCreateUserDto)),
    Pair("blank first name", objToJsonStr(blankFirstNameCreateUserDto)),
    Pair("blank last name", objToJsonStr(blankLastNameCreateUserDto)),
    Pair("blank password", objToJsonStr(blankPasswordCreateUserDto)),
    Pair("small strong password", objToJsonStr(smallPasswordCreateUserDto)),
    Pair("big strong password", objToJsonStr(bigPasswordCreateUserDto)),
    Pair("no capital letters password", objToJsonStr(noCapitalPasswordCreateUserDto)),
    Pair("no lower case letters password", objToJsonStr(noLowercasePasswordCreateUserDto)),
    Pair("no numbers password", objToJsonStr(noNumbersPasswordCreateUserDto)),
    Pair("no symbols password", objToJsonStr(noSymbolsPasswordCreateUserDto)),
    Pair("empty request body", ""),
    Pair(
      "null AWS username",
      "{\"awsUsername\":null,\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"Str0ngP455!\",\"tenantId\":1,\"role\":\"ADMIN\"}"
    ),
    Pair(
      "null first name",
      "{\"awsUsername\":\"john.doe\",\"firstName\":null,\"lastName\":\"Doe\",\"password\":\"Str0ngP455!\",\"tenantId\":1,\"role\":\"ADMIN\"}"
    ),
    Pair(
      "null last name",
      "{\"awsUsername\":\"john.doe\",\"firstName\":\"John\",\"lastName\":null,\"password\":\"Str0ngP455!\",\"tenantId\":1,\"role\":\"ADMIN\"}"
    ),
    Pair(
      "null password",
      "{\"awsUsername\":\"john.doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":null,\"tenantId\":1,\"role\":\"ADMIN\"}"
    ),
    Pair(
      "null tenantId",
      "{\"awsUsername\":\"john.doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"Str0ngP455!\",\"tenantId\":null,\"role\":\"ADMIN\"}"
    ),
    Pair(
      "null role",
      "{\"awsUsername\":\"john.doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"Str0ngP455!\",\"tenantId\":1,\"role\":null}"
    ),
    Pair(
      "invalid role",
      "{\"awsUsername\":\"john.doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"Str0ngP455!\",\"tenantId\":1,\"role\":\"SYSADMIN\"}"
    ),
  ).map { (testName: String, createUserDtoStr: String) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito.given(userService.create(strToJsonObj(createUserDtoStr)))
        .willReturn(1)

      mockMvc.post("/user") {
        contentType = MediaType.APPLICATION_JSON
        content = createUserDtoStr
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isBadRequest() }
      }
    }
  }
}

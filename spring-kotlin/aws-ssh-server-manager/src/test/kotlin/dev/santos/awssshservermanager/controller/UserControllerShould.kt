package dev.santos.awssshservermanager.controller

import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.UserService
import org.hamcrest.Matchers
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [UserController::class])
class UserControllerShould {
    @Autowired lateinit var mockMvc: MockMvc

    @MockBean private lateinit var passwordEncoder: PasswordEncoder
    @MockBean private val userService: UserService? = null

    @Test
    fun `return HTTP 201 when the user is created`() {
        val testUser = CreateUserDto(
                username = "john_doe",
                awsUsername = "johndoe",
                password = ""
        )

        Mockito.`when`(this.userService?.create(testUser)).thenReturn(1)

        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = Gson().toJson(testUser)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { MockMvcResultMatchers.status().isBadRequest }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("{\"id\":1}") }
            content { jsonPath<Int>("$.id", Matchers.`is`(1)) }
        }
    }

    @Test
    fun `return 409 when the user exists`() {
        val testUser = CreateUserDto(
            username = "john_doe",
            awsUsername = "johndoe",
            password = ""
        )
        Mockito.`when`(this.userService?.create(testUser)).thenThrow(DuplicateUserException("User -1 already exists!"))

        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = Gson().toJson(testUser)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
        }
    }

    @TestFactory
    fun `return 400 when the payload is invalid`() = listOf(
            Pair("username smaller than 5 chars", objToJsonStr(CreateUserDto(username = "john"))),
            Pair("username bigger than 50 chars", objToJsonStr(CreateUserDto(username = "A".repeat(51)))),
            Pair("username starts with number", objToJsonStr(CreateUserDto(username = "1johndoe"))),
            Pair("username starts with underscore", objToJsonStr(CreateUserDto(username = "_johndoe"))),
            Pair("username has a symbol", objToJsonStr(CreateUserDto(username = "jo\$hndoe"))),
            Pair("username has a symbol", objToJsonStr(CreateUserDto(username = "johndoe."))),
            Pair("empty request body", ""),
            Pair("null username", "{\"username\":null,\"awsUsername\":\"\",\"password\":\"\"}"),
            Pair("null awsUsername", "{\"username\":\"johndoe\",\"awsUsername\":null,\"password\":\"\"}"),
            Pair("null password", "{\"username\":\"johndoe\",\"awsUsername\":\"\",\"password\":null}"),
        ).map { (testName: String, createUserDtoStr: String) ->
            DynamicTest.dynamicTest(testName) {
                Mockito.`when`(userService?.create(strToJsonObj(createUserDtoStr))).thenReturn(1)

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
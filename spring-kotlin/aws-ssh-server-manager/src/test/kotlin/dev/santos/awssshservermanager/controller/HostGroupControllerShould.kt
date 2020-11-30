package dev.santos.awssshservermanager.controller

import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.exception.DuplicateHostGroupException
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.exception.HostGroupTenantNotFoundException
import dev.santos.awssshservermanager.exception.UserTenantNotFoundException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.HostGroupService
import dev.santos.awssshservermanager.service.UserService
import org.hamcrest.Matchers
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
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
@WebMvcTest(controllers = [HostGroupController::class])
class HostGroupControllerShould {
    @Autowired lateinit var mockMvc: MockMvc

    @MockBean private lateinit var passwordEncoder: PasswordEncoder
    @MockBean private lateinit var hostGroupService: HostGroupService

    private val validHostGroupDto = CreateHostGroupDto(
        tenantId = 1L,
        name = "production",
        matchers = listOf(
            HostGroupMatcherDto(
                "production_servers",
                listOf("production", "beanstalk")
            )
        )
    )

    private val blankNameCreateHostGroupDto = validHostGroupDto.copy(name = "")
    private val negativeTenantIdCreateHostGroupDto = validHostGroupDto.copy(tenantId = -1L)
    private val emptyMatchersCreateHostGroupDto = validHostGroupDto.copy(matchers = listOf())
    private val emptyMatcherTagNameCreateHostGroupDto = validHostGroupDto.copy(matchers = listOf(
        HostGroupMatcherDto(
            "",
            listOf("production", "beanstalk")
    )))
    private val emptyMatcherTagValuesCreateHostGroupDto = validHostGroupDto.copy(matchers = listOf(
        HostGroupMatcherDto(
            "production_servers",
            listOf()
    )))

    @Test
    fun `return HTTP 201 when the host group is created`() {
        BDDMockito.given(this.hostGroupService.create(validHostGroupDto))
            .willReturn(2)

        mockMvc.post("/hostgroup") {
            contentType = MediaType.APPLICATION_JSON
            content = Gson().toJson(validHostGroupDto)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("{\"id\":2}") }
            content { jsonPath<Int>("$.id", Matchers.`is`(2)) }
        }
    }

    @Test
    fun `return 409 when the host group exists`() {
        BDDMockito.given(this.hostGroupService.create(validHostGroupDto))
            .willAnswer{
                throw DuplicateHostGroupException("Host Group -1 already exists!")
            }

        mockMvc.post("/hostgroup") {
            contentType = MediaType.APPLICATION_JSON
            content = Gson().toJson(validHostGroupDto)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
        }
    }

    @Test
    fun `return 404 when the user tenant does not exist`() {
        BDDMockito.given(this.hostGroupService.create(validHostGroupDto))
                .willAnswer{
                    throw HostGroupTenantNotFoundException("Host Group Id -1 does not exist.")
                }

        mockMvc.post("/hostgroup") {
            contentType = MediaType.APPLICATION_JSON
            content = Gson().toJson(validHostGroupDto)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @TestFactory
    fun `return 400 when the payload is invalid`() = listOf(
        Pair("blank host group name", objToJsonStr(blankNameCreateHostGroupDto)),
        Pair("negative tenant id", objToJsonStr(negativeTenantIdCreateHostGroupDto)),
        Pair("empty matchers", objToJsonStr(emptyMatchersCreateHostGroupDto)),
        Pair("empty matcher tag name", objToJsonStr(emptyMatcherTagNameCreateHostGroupDto)),
        Pair("empty matcher tag value", objToJsonStr(emptyMatcherTagValuesCreateHostGroupDto)),
        Pair("empty request body", ""),
    ).map { (testName: String, createHostGroupDtoStr: String) ->
        DynamicTest.dynamicTest(testName) {
            BDDMockito.given(hostGroupService.create(strToJsonObj(createHostGroupDtoStr)))
                .willReturn(1)

            mockMvc.post("/hostgroup") {
                contentType = MediaType.APPLICATION_JSON
                content = createHostGroupDtoStr
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}
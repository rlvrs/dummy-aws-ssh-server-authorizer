package dev.santos.awssshservermanager.controller

import UnitTestBase
import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.exception.DuplicateHostGroupException
import dev.santos.awssshservermanager.exception.HostGroupTenantNotFoundException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.HostGroupService
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

@WebMvcTest(controllers = [HostGroupController::class])
class HostGroupControllerShould : UnitTestBase() {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var hostGroupService: HostGroupService

  private val validHostGroupMatcherDto = HostGroupMatcherDto(
    tagName = "production_servers",
    tagValues = listOf("production", "beanstalk")
  )
  private val validHostGroupDto = CreateHostGroupDto(
    tenantId = 1L,
    name = "production",
    matchers = listOf(
      validHostGroupMatcherDto,
      validHostGroupMatcherDto.copy(
        tagName = "development_servers",
        tagValues = listOf("development", "beanstalk")
      )
    )
  )

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
      .willAnswer {
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
      .willAnswer {
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
    Pair("negative tenant id", objToJsonStr(validHostGroupDto.copy(tenantId = -1L))),
    Pair("null tenant id", objToJsonStr(validHostGroupDto.copy(tenantId = null))),
    Pair("blank host group name", objToJsonStr(validHostGroupDto.copy(name = ""))),
    Pair("null host group name", objToJsonStr(validHostGroupDto.copy(name = null))),
    Pair("empty matchers", objToJsonStr(validHostGroupDto.copy(matchers = listOf()))),
    Pair("null matchers", objToJsonStr(validHostGroupDto.copy(matchers = null))),
    Pair(
      "empty matcher tag name", objToJsonStr(
        validHostGroupDto.copy(matchers = listOf(validHostGroupMatcherDto.copy(tagName = "")))
      )
    ),
    Pair(
      "null matcher tag name", objToJsonStr(
        validHostGroupDto.copy(matchers = listOf(validHostGroupMatcherDto.copy(tagName = null)))
      )
    ),
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

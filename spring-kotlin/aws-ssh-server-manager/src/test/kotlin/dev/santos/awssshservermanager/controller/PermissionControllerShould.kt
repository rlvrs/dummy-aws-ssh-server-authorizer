package dev.santos.awssshservermanager.controller

import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.exception.DuplicatePermissionException
import dev.santos.awssshservermanager.exception.PermissionTenantNotFoundException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.PermissionService
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
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [PermissionController::class])
class PermissionControllerShould {

  @Autowired
  lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var permissionService: PermissionService

  private val validPermissionDto = CreatePermissionDto(
    tenantId = 1L,
    grantorId = 1L,
    hostGroupId = 1L,
    grantee = "grantee.username",
    granteeType = "USER",
    expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
  )

  @Test
  fun `return HTTP 201 when the permission is created`() {
    BDDMockito.given(this.permissionService.create(validPermissionDto))
      .willReturn(2)

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validPermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":2}") }
      content { jsonPath<Int>("$.id", Matchers.`is`(2)) }
    }
  }

  @Test
  fun `return 409 when the permission exists`() {
    BDDMockito.given(this.permissionService.create(validPermissionDto))
      .willAnswer {
        throw DuplicatePermissionException("Permission Id -1 already exists!")
      }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validPermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isConflict() }
    }
  }

  @Test
  fun `return 404 when the permission tenant does not exist`() {
    BDDMockito.given(this.permissionService.create(validPermissionDto))
      .willAnswer {
        throw PermissionTenantNotFoundException("Tenant Id -1 does not exist.")
      }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validPermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isNotFound() }
    }
  }

  @TestFactory
  fun `return 400 when the payload is invalid`() = listOf(
    Pair("zero tenant id", objToJsonStr(validPermissionDto.copy(tenantId = 0L))),
    Pair("negative grantor id", objToJsonStr(validPermissionDto.copy(grantorId = -1L))),
    Pair("negative host group id", objToJsonStr(validPermissionDto.copy(hostGroupId = -2L))),
    Pair("blank grantee", objToJsonStr(validPermissionDto.copy(grantee = ""))),
    Pair("zero expiration time in minutes", objToJsonStr(validPermissionDto.copy(expirationTimeMinutes = 0L))),
    Pair("empty request body", ""),
    Pair(
      "null tenantId",
      objToJsonStr(validPermissionDto.copy(tenantId = null))
    ),
    Pair(
      "null grantor id",
      objToJsonStr(validPermissionDto.copy(grantorId = null))
    ),
    Pair(
      "null host group id",
      objToJsonStr(validPermissionDto.copy(hostGroupId = null))
    ),
    Pair(
      "null grantee",
      objToJsonStr(validPermissionDto.copy(grantee = null))
    ),
    Pair(
      "null grantee type",
      objToJsonStr(validPermissionDto.copy(granteeType = null))
    ),
    Pair(
      "null expiration time minutes",
      objToJsonStr(validPermissionDto.copy(expirationTimeMinutes = null))
    ),
    Pair(
      "invalid grantee type",
      objToJsonStr(validPermissionDto.copy(granteeType = "SYSADMIN"))
    ),
  ).map { (testName: String, createPermissionDtoStr: String) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito.given(permissionService.create(strToJsonObj(createPermissionDtoStr)))
        .willReturn(1)

      mockMvc.post("/permission") {
        contentType = MediaType.APPLICATION_JSON
        content = createPermissionDtoStr
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isBadRequest() }
      }
    }
  }
}

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

  private val validCreatePermissionDto = CreatePermissionDto(
    tenantId = 1L,
    grantorId = 1L,
    hostGroupId = 1L,
    grantee = "grantee.username",
    granteeType = "USER",
    expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
  )

  @Test
  fun `return HTTP 201 when the permission is created`() {
    BDDMockito.given(this.permissionService.create(validCreatePermissionDto))
      .willReturn(2)

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validCreatePermissionDto)
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
    BDDMockito.given(this.permissionService.create(validCreatePermissionDto))
      .willAnswer {
        throw DuplicatePermissionException("Permission Id -1 already exists!")
      }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validCreatePermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isConflict() }
    }
  }

  @Test
  fun `return 404 when the permission tenant does not exist`() {
    BDDMockito.given(this.permissionService.create(validCreatePermissionDto))
      .willAnswer {
        throw PermissionTenantNotFoundException("Tenant Id -1 does not exist.")
      }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validCreatePermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isNotFound() }
    }
  }

  @TestFactory
  fun `return 400 when the create permission dto is invalid`() = listOf(
    Pair("zero tenant id", objToJsonStr(validCreatePermissionDto.copy(tenantId = 0L))),
    Pair("negative grantor id", objToJsonStr(validCreatePermissionDto.copy(grantorId = -1L))),
    Pair("negative host group id", objToJsonStr(validCreatePermissionDto.copy(hostGroupId = -2L))),
    Pair("blank grantee", objToJsonStr(validCreatePermissionDto.copy(grantee = ""))),
    Pair(
      "negative expiration time in minutes",
      objToJsonStr(validCreatePermissionDto.copy(expirationTimeMinutes = -1L))
    ),
    Pair("empty request body", ""),
    Pair(
      "null tenantId",
      objToJsonStr(validCreatePermissionDto.copy(tenantId = null))
    ),
    Pair(
      "null grantor id",
      objToJsonStr(validCreatePermissionDto.copy(grantorId = null))
    ),
    Pair(
      "null host group id",
      objToJsonStr(validCreatePermissionDto.copy(hostGroupId = null))
    ),
    Pair(
      "null grantee",
      objToJsonStr(validCreatePermissionDto.copy(grantee = null))
    ),
    Pair(
      "null grantee type",
      objToJsonStr(validCreatePermissionDto.copy(granteeType = null))
    ),
    Pair(
      "null expiration time minutes",
      objToJsonStr(validCreatePermissionDto.copy(expirationTimeMinutes = null))
    ),
    Pair(
      "invalid grantee type",
      objToJsonStr(validCreatePermissionDto.copy(granteeType = "SYSADMIN"))
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

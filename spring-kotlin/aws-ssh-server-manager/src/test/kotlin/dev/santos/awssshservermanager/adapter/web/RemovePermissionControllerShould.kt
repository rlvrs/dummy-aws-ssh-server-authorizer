package dev.santos.awssshservermanager.adapter.web

import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand
import dev.santos.awssshservermanager.application.port.input.RemovePermissionUseCase
import dev.santos.awssshservermanager.exception.PermissionNotFoundException
import dev.santos.awssshservermanager.exception.PermissionTenantNotFoundException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.model.GranteeType
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Spy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [RemovePermissionController::class])
@ComponentScan(basePackageClasses = [RemovePermissionControllerShould::class])
class RemovePermissionControllerShould {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Spy
  private lateinit var removePermissionMapper: RemovePermissionMapper

  @MockBean
  private lateinit var removePermissionUseCase: RemovePermissionUseCase

  private val validRemovePermissionRequest = RemovePermissionRequest(
    id = 1L,
    tenantId = 1L,
    hostGroupId = 1L,
    grantee = "test.user",
    granteeType = "USER"
  )
  private val validRemovePermissionCommand = RemovePermissionCommand(
    id = 1L,
    tenantId = 1L,
    hostGroupId = 1L,
    grantee = "test.user",
    granteeType = GranteeType.USER
  )

  @Test
  fun `return HTTP 201 when the permission is removed`() {
    BDDMockito
      .given(this.removePermissionUseCase.removePermission(validRemovePermissionCommand))
      .willReturn(2)

    mockMvc.delete("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(validRemovePermissionRequest)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":2}") }
    }
  }

  @TestFactory
  fun `return 404 whilst removing permission`() = listOf(
    Triple(
      "permission tenant id does not exist",
      objToJsonStr(validRemovePermissionRequest),
      PermissionTenantNotFoundException("")
    ),
    Triple(
      "permission id does not exist",
      objToJsonStr(validRemovePermissionRequest),
      PermissionNotFoundException("")
    ),
  ).map { (testName: String, removePermissionDtoStr: String, exception: Exception) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito
        .doThrow(exception)
        .`when`(this.removePermissionUseCase).removePermission(strToJsonObj(removePermissionDtoStr))

      mockMvc.delete("/permission") {
        contentType = MediaType.APPLICATION_JSON
        content = removePermissionDtoStr
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isNotFound() }
      }
    }
  }

  @Test
  fun `return 400 when the payload is empty`() {
    BDDMockito.given(removePermissionUseCase.removePermission(strToJsonObj("")))
      .willReturn(1)

    mockMvc.delete("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = ""
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isBadRequest() }
    }
  }

  @TestFactory
  fun `return 400 when the create permission dto is invalid`() = listOf(
    Pair("negative id", validRemovePermissionRequest.copy(id = -1L)),
    Pair("zero tenant id", validRemovePermissionRequest.copy(tenantId = 0L)),
    Pair("negative host group id", validRemovePermissionRequest.copy(hostGroupId = -2L)),
    Pair("blank grantee", validRemovePermissionRequest.copy(grantee = "")),
    Pair("blank grantee type", validRemovePermissionRequest.copy(granteeType = "")),
    Pair(
      "null id",
      validRemovePermissionRequest.copy(id = null)
    ),
    Pair(
      "null tenantId",
      validRemovePermissionRequest.copy(tenantId = null)
    ),
    Pair(
      "null host group id",
      validRemovePermissionRequest.copy(hostGroupId = null)
    ),
    Pair(
      "null grantee",
      validRemovePermissionRequest.copy(grantee = null)
    ),
    Pair(
      "null grantee type",
      validRemovePermissionRequest.copy(granteeType = null)
    ),
    Pair(
      "invalid grantee type",
      validRemovePermissionRequest.copy(granteeType = "SYSADMIN")
    ),
  ).map { (testName: String, removePermissionRequest: RemovePermissionRequest) ->
    DynamicTest.dynamicTest(testName) {
      val command = removePermissionMapper.toRemovePermissionCommand(removePermissionRequest)
      BDDMockito.given(removePermissionUseCase.removePermission(command))
        .willReturn(1)

      mockMvc.delete("/permission") {
        contentType = MediaType.APPLICATION_JSON
        content = objToJsonStr(removePermissionRequest)
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isBadRequest() }
      }
    }
  }
}

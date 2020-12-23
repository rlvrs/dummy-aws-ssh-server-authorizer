package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.adapter.web.removepermission.RemovePermissionRequest
import dev.santos.awssshservermanager.adapter.web.removepermission.RemovePermissionResponse
import dev.santos.awssshservermanager.helper.objToJsonStr
import org.springframework.boot.test.context.TestComponent
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete

@TestComponent
class RemovePermissionFixture(
  val mockMvc: MockMvc
) {
  val request = RemovePermissionRequest(
    id = 1L,
    tenantId = 1L,
    hostGroupId = 1L,
    grantee = "test.user",
    granteeType = "USER",
  )

  fun removePermission() {
    mockMvc.delete("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(request)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(objToJsonStr(RemovePermissionResponse(1L))) }
    }
  }
}

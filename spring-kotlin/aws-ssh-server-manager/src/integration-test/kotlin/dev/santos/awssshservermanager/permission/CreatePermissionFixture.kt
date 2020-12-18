package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.adapter.persistence.PermissionRepository
import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.mapper.PermissionMapper
import dev.santos.awssshservermanager.model.Permission
import org.springframework.boot.test.context.TestComponent
import org.springframework.data.domain.Example
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.concurrent.TimeUnit

@TestComponent
class CreatePermissionFixture(
  val permissionRepository: PermissionRepository,
  val permissionMapper: PermissionMapper,
  val mockMvc: MockMvc
) {
  val createPermissionDto = CreatePermissionDto(
    tenantId = 1L,
    grantorId = 1L,
    hostGroupId = 1L,
    grantee = "test.user",
    granteeType = "USER",
    expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
  )

  fun createPermission() {
    val permission: Permission = permissionMapper.toPermission(createPermissionDto)
    if (permissionRepository.findOne(Example.of(permission)).isPresent) {
      return
    }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(createPermissionDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":1}") }
    }
  }
}

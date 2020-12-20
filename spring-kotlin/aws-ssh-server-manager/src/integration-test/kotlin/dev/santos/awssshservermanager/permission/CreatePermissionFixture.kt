package dev.santos.awssshservermanager.permission

import dev.santos.awssshservermanager.adapter.persistence.PermissionRepository
import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.mapper.PermissionMapper
import dev.santos.awssshservermanager.model.Permission
import org.hamcrest.Matchers
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

  private fun createPermission(dto: CreatePermissionDto) {
    val permission: Permission = permissionMapper.toPermission(dto)
    if (permissionRepository.findOne(Example.of(permission)).isPresent) {
      return
    }

    mockMvc.post("/permission") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(dto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { jsonPath<Int>("$.id", Matchers.greaterThan(0)) }
    }
  }

  fun createPermission() {
    createPermission(createPermissionDto)
  }

  fun createExpiredPermission() {
    createPermission(createPermissionDto.copy(expirationTimeMinutes = 0L))
  }
}

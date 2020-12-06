package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreatePermissionDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

@SpringBootTest
@ExtendWith(SpringExtension::class)
class PermissionMapperShould {
  @Autowired
  lateinit var permissionMapper: PermissionMapper

  @TestFactory
  fun `map to Permission valid CreatePermissionDto`() = listOf(
    Pair(
      "create permission dto with all fields",
      CreatePermissionDto(
        tenantId = 1L,
        grantorId = 1L,
        hostGroupId = 1L,
        grantee = "grantee.username",
        granteeType = "USER",
        expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
      )
    ),
    Pair(
      "create permission dto with some fields",
      CreatePermissionDto(
        tenantId = 1L,
        grantorId = 1L,
        hostGroupId = 1L,
        granteeType = "GROUP",
        expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
      )
    )
  ).map { (testName: String, createPermissionDto: CreatePermissionDto) ->
    DynamicTest.dynamicTest(testName) {
      val mappedPermission = permissionMapper.toPermission(createPermissionDto)

      assertThat(mappedPermission).isNotNull
      assertThat(mappedPermission.tenantId).isEqualTo(createPermissionDto.tenantId)
      assertThat(mappedPermission.grantorId).isEqualTo(createPermissionDto.grantorId)
      assertThat(mappedPermission.hostGroupId).isEqualTo(createPermissionDto.hostGroupId)
      assertThat(mappedPermission.grantee).isEqualTo(createPermissionDto.grantee)
      assertThat(mappedPermission.granteeType.name).isEqualTo(createPermissionDto.granteeType)
      assertThat(mappedPermission.expirationTimeMinutes).isEqualTo(createPermissionDto.expirationTimeMinutes)
    }
  }
}

package dev.santos.awssshservermanager.adapter.web.getvalidpermissions

import UnitTestBase
import dev.santos.awssshservermanager.model.GranteeType
import dev.santos.awssshservermanager.model.HostGroup
import dev.santos.awssshservermanager.model.HostGroupMatcher
import dev.santos.awssshservermanager.model.Permission
import dev.santos.awssshservermanager.model.User
import dev.santos.awssshservermanager.model.UserRole
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

@SpringBootTest
class GetValidPermissionsMapperShould : UnitTestBase() {
  @Autowired
  lateinit var getValidPermissionsMapper: GetValidPermissionsMapper

  @TestFactory
  fun `map Permission to GetValidPermission`() = listOf(
    Pair(
      "create GetValidPermission with all fields",
      Permission(
        tenantId = 1L,
        grantor = User(
          id = 1L,
          tenantId = 1L,
          awsUsername = "john.doe",
          firstName = "John",
          lastName = "Doe",
          password = "super-sensitive",
          role = UserRole.ADMIN
        ),
        hostGroup = HostGroup(
          id = 1L,
          tenantId = 1L,
          name = "test-dev",
          matchers = listOf(
            HostGroupMatcher(tagName = "environment", listOf("test-dev")),
            HostGroupMatcher(tagName = "deployment", listOf("none")),
          ),
          policyArn = "random-arn",
          policyVersionId = "random-policy-version-id"
        ),
        grantee = "grantee.username",
        granteeType = GranteeType.USER,
        expirationTimeMinutes = TimeUnit.MINUTES.toMillis(10L)
      )
    )
  ).map { (testName: String, permission: Permission) ->
    DynamicTest.dynamicTest(testName) {
      val mappedPermission = getValidPermissionsMapper.toGetValidPermissionsResponse(permission)

      Assertions.assertThat(mappedPermission).isNotNull
      Assertions.assertThat(mappedPermission.tenantId).isEqualTo(permission.tenantId)
      Assertions.assertThat(mappedPermission.grantor.id).isEqualTo(permission.grantor.id)
      Assertions.assertThat(mappedPermission.grantor.firstName).isEqualTo(permission.grantor.firstName)
      Assertions.assertThat(mappedPermission.grantor.lastName).isEqualTo(permission.grantor.lastName)
      Assertions.assertThat(mappedPermission.grantor.awsUsername).isEqualTo(permission.grantor.awsUsername)
      Assertions.assertThat(mappedPermission.hostGroup.id).isEqualTo(permission.hostGroup.id)
      Assertions.assertThat(mappedPermission.hostGroup.name).isEqualTo(permission.hostGroup.name)
      Assertions.assertThat(mappedPermission.grantee).isEqualTo(permission.grantee)
      Assertions.assertThat(mappedPermission.granteeType.name).isEqualTo(permission.granteeType.name)
    }
  }
}

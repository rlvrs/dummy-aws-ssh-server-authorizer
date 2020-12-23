package dev.santos.awssshservermanager.mapper

import UnitTestBase
import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.CreateUserDtoRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class UserMapperShould : UnitTestBase() {
  @Autowired
  lateinit var userMapper: UserMapper

  @Autowired
  lateinit var passwordEncoder: PasswordEncoder

  @TestFactory
  fun `map to User valid CreateUserDto`() = listOf(
    Pair(
      "create user dto with all fields",
      CreateUserDto(
        tenantId = 1L,
        awsUsername = "some-aws-username",
        password = "5uP3r_53cr3t",
        firstName = "some-first-name",
        lastName = "some-last-name",
        role = CreateUserDtoRole.ADMIN
      )
    ),
    Pair(
      "create user dto with name and key",
      CreateUserDto(
        tenantId = 1L,
        awsUsername = "some-aws-username",
        firstName = "some-first-name"
      )
    ),
  ).map { (testName: String, createUserDto: CreateUserDto) ->
    DynamicTest.dynamicTest(testName) {
      val mappedUser = userMapper.toUser(createUserDto)

      assertThat(mappedUser).isNotNull
      assertThat(mappedUser.tenantId).isEqualTo(createUserDto.tenantId)
      assertThat(mappedUser.awsUsername).isEqualTo(createUserDto.awsUsername)
      assertThat(mappedUser.firstName).isEqualTo(createUserDto.firstName)
      assertThat(mappedUser.lastName).isEqualTo(createUserDto.lastName)
      assertThat(mappedUser.role.name).isEqualTo(createUserDto.role.name)
      Assertions.assertTrue(passwordEncoder.matches(createUserDto.password, mappedUser.password))
    }
  }
}

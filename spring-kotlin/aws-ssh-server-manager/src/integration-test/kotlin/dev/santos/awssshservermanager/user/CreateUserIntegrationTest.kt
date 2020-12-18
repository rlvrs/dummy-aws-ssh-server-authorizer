package dev.santos.awssshservermanager.user

import dev.santos.awssshservermanager.IntegrationTestBase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CreateUserIntegrationTest : IntegrationTestBase() {
  @Test
  fun `creates user successfully`() {
    createTenantFixture.createTenant(tenantAwsAccessKey)
    createUserFixture.createUser()

    val newUser = userRepository.findById(1L).get()
    Assertions.assertThat(newUser.id).isEqualTo(1L)
    Assertions.assertThat(newUser.awsUsername).isEqualTo(createUserFixture.createUserDto.awsUsername)
    Assertions.assertThat(newUser.firstName).isEqualTo(createUserFixture.createUserDto.firstName)
    Assertions.assertThat(newUser.lastName).isEqualTo(createUserFixture.createUserDto.lastName)
    Assertions.assertThat(newUser.role.name).isEqualTo(createUserFixture.createUserDto.role.name)
    Assertions.assertThat(newUser.tenantId).isEqualTo(createUserFixture.createUserDto.tenantId)
    Assertions.assertThat(passwordEncoder.matches(createUserFixture.createUserDto.password, newUser.password)).isTrue
  }
}

package dev.santos.awssshservermanager.dto

import dev.santos.awssshservermanager.model.User
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

enum class UserRole {
  ADMIN, SYSADMIN
}

data class CreateUserDto(
  @field:NotBlank
  val awsUsername: String="",
  @field:NotBlank
  val firstName: String="",
  @field:NotBlank
  val lastName: String="",
  @field:Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).*\$")
  @field:Size(min=8, max=50)
  val password: String="",
  @field:Positive
  val tenantId: Long
) {
  fun toUserAdmin(encryptedPassword: String): User {
    return User(
      tenantId = this.tenantId,
      awsUsername = this.awsUsername,
      firstName = this.firstName,
      lastName = this.lastName,
      password = encryptedPassword,
      role = UserRole.ADMIN.name
    )
  }
}

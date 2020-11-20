package dev.santos.awssshservermanager.dto

import javax.validation.constraints.*

data class CreateUserDto(
  @field:Pattern(regexp = "^[a-zA-Z][\\w._]+\\w\$")
  @field:Size(min=5, max=50)
  val username: String="",
  val awsUsername: String="",
  val password: String=""
)

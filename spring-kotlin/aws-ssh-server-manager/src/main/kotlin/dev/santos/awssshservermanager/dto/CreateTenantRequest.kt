package dev.santos.awssshservermanager.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateTenantRequest(
  @field:Pattern(regexp = "^[a-z]+(-[a-z\\d]+)*\$")
  @field:Size(min = 2, max = 250)
  @field:NotNull
  val name: String?,
  @field:NotNull
  val awsApiKey: String?,
  @field:NotNull
  val awsApiSecret: String?
)

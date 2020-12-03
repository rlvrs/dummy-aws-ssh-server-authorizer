package dev.santos.awssshservermanager.dto

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateTenantDto(
  @field:Pattern(regexp = "^[a-z]+(-[a-z\\d]+)*\$")
  @field:Size(min = 2, max = 50)
  val name: String = "",
  val awsApiKey: String = "",
  val awsApiSecret: String = ""
)

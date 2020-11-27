package dev.santos.awssshservermanager.dto

import dev.santos.awssshservermanager.model.Tenant
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateTenantDto(
  @field:Pattern(regexp = "^[a-z]+(-[a-z\\d]+)*\$")
  @field:Size(min=2, max=50)
  val name: String="",
  val awsApiKey: String="",
  val awsApiSecret: String=""
) {
  fun toTenant(): Tenant {
    return Tenant(
      name = this.name,
      awsApiKeySsmName = "${this.name}-api-key",
      awsApiSecretSsmName = "${this.name}-api-secret"
    )
  }
}

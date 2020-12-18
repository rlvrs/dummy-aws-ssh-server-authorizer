package dev.santos.awssshservermanager.adapter.web

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive

data class RemovePermissionRequest(
  @field:Positive
  @field:NotNull
  val id: Long?,
  @field:Positive
  @field:NotNull
  val tenantId: Long?,
  @field:NotNull
  @field:Positive
  val hostGroupId: Long?,
  @field:NotNull
  @field:NotBlank
  val grantee: String? = "",
  @field:NotNull
  @field:NotBlank
  @field:Pattern(regexp = "USER|GROUP")
  val granteeType: String? = "",
)

data class RemovePermissionResponse(
  val id: Long
)

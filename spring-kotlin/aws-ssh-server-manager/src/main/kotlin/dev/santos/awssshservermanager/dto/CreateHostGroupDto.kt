package dev.santos.awssshservermanager.dto

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class HostGroupMatcherDto(
  @field:NotNull
  @field:NotBlank
  val tagName: String?,
  @field:NotNull
  @field:NotEmpty
  val tagValues: List<String>?
)

data class CreateHostGroupDto(
  @field:NotNull
  @field:Positive
  val tenantId: Long?,
  @field:NotNull
  @field:NotBlank
  val name: String? = "",
  @field:NotNull
  @field:Valid
  @field:NotEmpty
  val matchers: List<HostGroupMatcherDto>?
)

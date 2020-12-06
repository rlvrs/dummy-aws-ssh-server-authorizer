package dev.santos.awssshservermanager.dto

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class CreateHostGroupPolicyMatcherDto(
  @field:NotBlank
  val tagName: String,
  @field:NotEmpty
  val tagValues: List<String>
)

data class CreateHostGroupPolicyDto(
  @field:Positive
  val tenantId: Long,
  @field:NotBlank
  val name: String = "",
  @field:Valid
  @field:NotEmpty
  val matchers: List<CreateHostGroupPolicyMatcherDto>
)

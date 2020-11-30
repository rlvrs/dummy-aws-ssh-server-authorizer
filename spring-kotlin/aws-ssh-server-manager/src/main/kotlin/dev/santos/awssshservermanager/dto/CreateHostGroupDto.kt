package dev.santos.awssshservermanager.dto

import dev.santos.awssshservermanager.model.HostGroup
import dev.santos.awssshservermanager.model.HostGroupMatcher
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class HostGroupMatcherDto(
  @field:NotBlank
  val tagName: String,
  @field:NotEmpty
  val tagValues: List<String>
)

data class CreateHostGroupDto(
  @field:Positive
  val tenantId:Long,
  @field:NotBlank
  val name: String="",
  @field:Valid
  @field:NotEmpty
  val matchers: List<HostGroupMatcherDto>
) {
  fun toHostGroup(): HostGroup {
    return HostGroup(
      tenantId = this.tenantId,
      name = this.name,
      matchers = this.matchers.map(this::toHostGroupMatcher)
    )
  }

  private fun toHostGroupMatcher(hostGroupMatcherDto: HostGroupMatcherDto): HostGroupMatcher {
    return HostGroupMatcher(
      tagName = hostGroupMatcherDto.tagName,
      tagValues = hostGroupMatcherDto.tagValues
    )
  }
}

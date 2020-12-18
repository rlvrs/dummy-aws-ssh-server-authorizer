package dev.santos.awssshservermanager.application.port.input

import dev.santos.awssshservermanager.exception.PermissionNotFoundException
import dev.santos.awssshservermanager.model.GranteeType
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class RemovePermissionCommand(
  @field:Positive
  val id: Long,
  @field:Positive
  val tenantId: Long,
  @field:Positive
  val hostGroupId: Long,
  @field:NotBlank
  val grantee: String,
  @field:NotBlank
  val granteeType: GranteeType,
)

interface RemovePermissionUseCase {
  @Throws(PermissionNotFoundException::class)
  fun removePermission(@Valid removePermissionCommand: RemovePermissionCommand): Long
}

package dev.santos.awssshservermanager.adapter.web.getvalidpermissions

import dev.santos.awssshservermanager.model.GranteeType

data class GetValidPermissionsHostGroupResponse(
  val id: Long,
  val name: String,
)

data class GetValidPermissionsUserResponse(
  val id: Long,
  val awsUsername: String,
  val firstName: String,
  val lastName: String,
)

data class GetValidPermissionResponse(
  val id: Long,
  val tenantId: Long,
  val hostGroup: GetValidPermissionsHostGroupResponse,
  val grantor: GetValidPermissionsUserResponse,
  val grantee: String,
  val granteeType: GranteeType,
)

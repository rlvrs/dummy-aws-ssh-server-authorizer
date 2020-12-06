package dev.santos.awssshservermanager.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

enum class GranteeType {
  USER, GROUP
}

@Entity(name = "permission")
@Table
class Permission(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var tenantId: Long = 0,
  var grantorId: Long = 0,
  var hostGroupId: Long = 0,
  var grantee: String = "",
  @Enumerated(EnumType.STRING)
  var granteeType: GranteeType = GranteeType.USER,
  var expirationTimeMinutes: Long? = null
)

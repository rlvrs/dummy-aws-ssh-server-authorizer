package dev.santos.awssshservermanager.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

enum class UserRole {
  ADMIN, SYSADMIN
}

@Entity(name = "users")
@Table
class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var tenantId: Long = 0,
  var awsUsername: String = "",
  var firstName: String = "",
  var lastName: String = "",
  var password: String = "",
  @Enumerated(EnumType.STRING)
  var role: UserRole = UserRole.ADMIN
)

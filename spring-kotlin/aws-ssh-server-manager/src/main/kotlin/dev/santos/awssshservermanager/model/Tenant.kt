package dev.santos.awssshservermanager.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "tenant")
@Table
class Tenant(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var name: String = "",
  var awsApiKey: String = "",
  var awsApiSecret: String = ""
)

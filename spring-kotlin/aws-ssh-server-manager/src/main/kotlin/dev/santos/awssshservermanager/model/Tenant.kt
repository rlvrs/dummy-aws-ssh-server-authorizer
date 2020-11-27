package dev.santos.awssshservermanager.model

import javax.persistence.*

@Entity(name = "tenant")
@Table
class Tenant(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id:Long=0,
  val name: String = "",
  val awsApiKeySsmName: String = "",
  val awsApiSecretSsmName: String = ""
)
package dev.santos.awssshservermanager.model

import javax.persistence.*

@Entity(name = "users")
@Table
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id:Long=0,
        val tenantId:Long=0,
        val awsUsername: String="",
        val firstName: String="",
        val lastName: String="",
        val password: String="",
        val role: String=""
)
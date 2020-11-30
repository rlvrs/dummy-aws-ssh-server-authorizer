package dev.santos.awssshservermanager.repository

import dev.santos.awssshservermanager.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
}
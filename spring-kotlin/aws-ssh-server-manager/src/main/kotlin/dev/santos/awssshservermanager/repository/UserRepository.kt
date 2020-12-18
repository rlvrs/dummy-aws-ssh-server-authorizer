package dev.santos.awssshservermanager.repository

import dev.santos.awssshservermanager.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
  @Query(
    "SELECT u " +
      "FROM users u " +
      "WHERE u.tenantId = :tenant_id " +
      "AND u.awsUsername = :aws_username"
  )
  fun findByTenantAndAwsUsername(
    @Param("tenant_id") tenantId: Long,
    @Param("aws_username") awsUsername: String
  ): Optional<User>
}

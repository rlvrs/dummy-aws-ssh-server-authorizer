package dev.santos.awssshservermanager.adapter.persistence

import dev.santos.awssshservermanager.model.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
  @Transactional
  @Modifying
  @Query(
    "DELETE " +
      "FROM permission p " +
      "WHERE p.tenantId = :tenant_id " +
      "AND p.id = :permissionId"
  )
  fun deleteByTenantAndId(
    @Param("tenant_id") tenantId: Long,
    @Param("permissionId") permissionId: Long
  )

  @Query(
    "SELECT * " +
      "FROM permission " +
      "WHERE created_ts + expiration_time_minutes * interval '1 minute' < timezone('utc', now())",
    nativeQuery = true
  )
  fun findAllExpired(): List<Permission>

  @Query(
    "SELECT * " +
      "FROM permission p " +
      "JOIN users u ON p.grantor_id=u.id " +
      "JOIN host_group hg ON p.host_group_id=hg.id " +
      "WHERE p.tenant_id = :tenant_id " +
      "AND created_ts + expiration_time_minutes * interval '1 minute' > timezone('utc', now())",
    nativeQuery = true
  )
  fun findValidPermissions(
    @Param("tenant_id") tenantId: Long
  ): List<Permission>
}

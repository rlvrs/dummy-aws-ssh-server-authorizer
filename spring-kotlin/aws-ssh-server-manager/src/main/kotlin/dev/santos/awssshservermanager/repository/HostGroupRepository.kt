package dev.santos.awssshservermanager.repository

import dev.santos.awssshservermanager.model.HostGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface HostGroupRepository : JpaRepository<HostGroup, Long> {
  @Query(
    "SELECT hg " +
      "FROM host_group hg " +
      "WHERE hg.tenantId = :tenant_id " +
      "AND hg.id = :id"
  )
  fun findByTenantAndId(
    @Param("tenant_id") tenantId: Long,
    @Param("id") id: Long
  ): HostGroup?

  @Query(
    "SELECT hg " +
      "FROM host_group hg " +
      "WHERE hg.tenantId = :tenant_id " +
      "AND hg.name = :name"
  )
  fun findByTenantAndName(
    @Param("tenant_id") tenantId: Long,
    @Param("name") name: String
  ): HostGroup?
}

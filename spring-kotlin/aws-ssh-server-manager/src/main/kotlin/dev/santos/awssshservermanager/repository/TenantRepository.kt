package dev.santos.awssshservermanager.repository

import dev.santos.awssshservermanager.model.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantRepository: JpaRepository<Tenant, Long> {
    fun findByName(name: String): Tenant?
}
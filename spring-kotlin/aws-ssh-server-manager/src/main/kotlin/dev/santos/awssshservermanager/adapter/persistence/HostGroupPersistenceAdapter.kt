package dev.santos.awssshservermanager.adapter.persistence

import dev.santos.awssshservermanager.application.port.output.GetHostGroupPort
import dev.santos.awssshservermanager.exception.HostGroupNotFoundException
import dev.santos.awssshservermanager.model.HostGroup
import dev.santos.awssshservermanager.repository.HostGroupRepository
import org.springframework.stereotype.Component

@Component
class HostGroupPersistenceAdapter(
  private val hostGroupRepository: HostGroupRepository
) : GetHostGroupPort {
  @Throws(HostGroupNotFoundException::class)
  override fun getHostGroup(tenantId: Long, hostGroupId: Long): HostGroup {
    return hostGroupRepository.findByTenantAndId(tenantId, hostGroupId)
      ?: throw HostGroupNotFoundException("Host Group [${hostGroupId}] for tenant [${tenantId}] not found!")
  }
}

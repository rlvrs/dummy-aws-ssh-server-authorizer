package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.exception.HostGroupNotFoundException
import dev.santos.awssshservermanager.model.HostGroup

interface GetHostGroupPort {
  @Throws(HostGroupNotFoundException::class)
  fun getHostGroup(tenantId: Long, hostGroupId: Long): HostGroup
}

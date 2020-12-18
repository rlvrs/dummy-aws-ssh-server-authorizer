package dev.santos.awssshservermanager.hostgroup

import dev.santos.awssshservermanager.IntegrationTestBase
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CreateHostGroupIntegrationTest : IntegrationTestBase() {
  @Test
  fun `creates host group successfully`() {
    createTenantFixture.createTenant(tenantAwsAccessKey)
    createUserFixture.createUser()
    createHostGroupFixture.createHostGroup()

    val newHostGroup = hostGroupRepository.findById(1L).get()
    Assertions.assertThat(newHostGroup.id).isEqualTo(1L)
    Assertions.assertThat(newHostGroup.name).isEqualTo(createHostGroupFixture.createHostGroupDto.name)
    Assertions.assertThat(newHostGroup.tenantId).isEqualTo(createHostGroupFixture.createHostGroupDto.tenantId)
    val mappedMatchers = hostGroupMapper.toCreateHostGroupDto(newHostGroup).matchers
    Assertions.assertThat(mappedMatchers).isEqualTo(createHostGroupFixture.createHostGroupDto.matchers)
  }
}

package dev.santos.awssshservermanager.hostgroup

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.repository.HostGroupRepository
import org.springframework.boot.test.context.TestComponent
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@TestComponent
class CreateHostGroupFixture(
  val hostGroupRepository: HostGroupRepository,
  val mockMvc: MockMvc
) {
  private val validHostGroupMatcherDto = HostGroupMatcherDto(
    tagName = "production_servers",
    tagValues = listOf("production", "beanstalk")
  )
  val createHostGroupDto = CreateHostGroupDto(
    tenantId = 1L,
    name = "production",
    matchers = listOf(
      validHostGroupMatcherDto,
      validHostGroupMatcherDto.copy(
        tagName = "development_servers",
        tagValues = listOf("development", "beanstalk")
      )
    )
  )

  fun createHostGroup() {
    val foundHostGroup: Boolean? = createHostGroupDto.tenantId?.let { hgTenant ->
      createHostGroupDto.name?.let { hgName ->
        val hg = hostGroupRepository.findByTenantAndName(
          hgTenant,
          hgName
        )
        hg != null
      }
    }
    if (foundHostGroup == true) {
      return
    }

    mockMvc.post("/hostgroup") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(createHostGroupDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":1}") }
    }
  }
}

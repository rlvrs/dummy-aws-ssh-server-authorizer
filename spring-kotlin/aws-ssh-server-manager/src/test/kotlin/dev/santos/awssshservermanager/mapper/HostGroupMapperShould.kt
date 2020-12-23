package dev.santos.awssshservermanager.mapper

import UnitTestBase
import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HostGroupMapperShould : UnitTestBase() {
  @Autowired
  lateinit var hostGroupMapper: HostGroupMapper

  @TestFactory
  fun `map to HostGroup valid CreateHostGroupDto`() = listOf(
    Triple(
      "create host group dto with all fields",
      CreateHostGroupDto(
        tenantId = 1L,
        name = "production",
        matchers = listOf(
          HostGroupMatcherDto(
            "production_servers",
            listOf("production", "beanstalk")
          )
        )
      ),
      IamPolicy("", "v1", "some-arn")
    ),
    Triple(
      "create host group dto with some fields",
      CreateHostGroupDto(
        tenantId = 1L,
        matchers = listOf(
          HostGroupMatcherDto(
            "production_servers",
            listOf("production", "beanstalk")
          )
        )
      ),
      IamPolicy("", "v1", "some-arn")
    )
  ).map { (testName: String, createHostGroupDto: CreateHostGroupDto, iamPolicy: IamPolicy) ->
    DynamicTest.dynamicTest(testName) {
      val mappedHostGroup = hostGroupMapper.toHostGroup(createHostGroupDto, iamPolicy)

      assertThat(mappedHostGroup).isNotNull
      assertThat(mappedHostGroup.name).isEqualTo(createHostGroupDto.name)
      assertThat(mappedHostGroup.tenantId).isEqualTo(createHostGroupDto.tenantId)
      assertThat(mappedHostGroup.policyArn).isEqualTo(iamPolicy.arn)
      assertThat(mappedHostGroup.policyVersionId).isEqualTo(iamPolicy.versionId)
      mappedHostGroup.matchers.map { it.tagName to it.tagValues }
        .containsAll(createHostGroupDto.matchers!!.map { it.tagName to it.tagValues })
    }
  }

  @TestFactory
  fun `map to HostGroup a valid CreateHostGroupDto with no policy info`() = listOf(
    Pair(
      "create host group dto with all fields",
      CreateHostGroupDto(
        tenantId = 1L,
        name = "production",
        matchers = listOf(
          HostGroupMatcherDto(
            "production_servers",
            listOf("production", "beanstalk")
          )
        )
      )
    ),
    Pair(
      "create host group dto with some fields",
      CreateHostGroupDto(
        tenantId = 1L,
        matchers = listOf(
          HostGroupMatcherDto(
            "production_servers",
            listOf("production", "beanstalk")
          )
        )
      )
    )
  ).map { (testName: String, createHostGroupDto: CreateHostGroupDto) ->
    DynamicTest.dynamicTest(testName) {
      val mappedHostGroup = hostGroupMapper.toCreateHostGroupPolicyDto(createHostGroupDto)

      assertThat(mappedHostGroup).isNotNull
      assertThat(mappedHostGroup.name).isEqualTo(createHostGroupDto.name)
      assertThat(mappedHostGroup.tenantId).isEqualTo(createHostGroupDto.tenantId)
      mappedHostGroup.matchers.map { it.tagName to it.tagValues }
        .containsAll(createHostGroupDto.matchers!!.map { it.tagName to it.tagValues })
    }
  }
}

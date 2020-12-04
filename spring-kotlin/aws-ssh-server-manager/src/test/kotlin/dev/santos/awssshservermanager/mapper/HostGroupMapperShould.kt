package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class HostGroupMapperShould {
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
      val mappedTenant = hostGroupMapper.toHostGroup(createHostGroupDto, iamPolicy)

      assertThat(mappedTenant).isNotNull
      assertThat(mappedTenant.name).isEqualTo(createHostGroupDto.name)
      assertThat(mappedTenant.tenantId).isEqualTo(createHostGroupDto.tenantId)
      assertThat(mappedTenant.policyArn).isEqualTo(iamPolicy.arn)
      assertThat(mappedTenant.policyVersionId).isEqualTo(iamPolicy.versionId)
      mappedTenant.matchers.map { it.tagName to it.tagValues }
        .containsAll(createHostGroupDto.matchers.map { it.tagName to it.tagValues })
    }
  }
}

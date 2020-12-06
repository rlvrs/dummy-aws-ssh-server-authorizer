package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.helper.ResourceHelper
import dev.santos.awssshservermanager.helper.minifyJsonStr
import dev.santos.awssshservermanager.helper.safeEq
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import dev.santos.awssshservermanager.mapper.HostGroupMapper
import dev.santos.awssshservermanager.model.HostGroup
import dev.santos.awssshservermanager.model.HostGroupMatcher
import dev.santos.awssshservermanager.repository.HostGroupRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

@SpringBootTest
@ExtendWith(SpringExtension::class)
class HostGroupServiceShould {
  @Mock
  private lateinit var hostGroupRepository: HostGroupRepository

  @Mock
  private lateinit var policyManager: PolicyManager

  @Mock
  private lateinit var tenantService: TenantService

  @Mock
  private lateinit var hostGroupMapper: HostGroupMapper

  @InjectMocks
  private lateinit var hostGroupService: HostGroupService

  private val awsCredentials: AwsBasicCredentials = AwsBasicCredentials.create("0", "0")
  private val validInputDto = CreateHostGroupDto(
    tenantId = 1L,
    name = "AllowSsmProduction",
    matchers = listOf(
      HostGroupMatcherDto("Environment", listOf("production")),
      HostGroupMatcherDto("Deployment", listOf("beanstalk"))
    )
  )
  private val validHostGroup = HostGroup(
    tenantId = 1L,
    name = validInputDto.name!!,
    matchers = validInputDto.matchers!!.map {
      HostGroupMatcher(
        it.tagName!!,
        it.tagValues!!
      )
    },
    policyArn = "",
    policyVersionId = ""
  )

  @Test
  fun `generate a document successfully`() {
    val expectedDocument = ResourceHelper.readAsString("/service/hostgroup/expectedPolicyDocument.json")

    val generatedDocument = hostGroupService.generateDocument(validHostGroup.name, validHostGroup.matchers)

    assertThat(minifyJsonStr(generatedDocument)).isEqualTo(minifyJsonStr(expectedDocument))
  }

  @Test
  fun `create a host group successfully`() {
    val expectedId = 2L
    val expectedIamPolicy = IamPolicy(
      document = minifyJsonStr(ResourceHelper.readAsString("/service/hostgroup/expectedPolicyDocument.json")),
      versionId = "v2",
      arn = "policy_arn"
    )
    val expectedHostGroup = HostGroup(
      id = expectedId,
      tenantId = validHostGroup.tenantId,
      name = validHostGroup.name,
      matchers = validHostGroup.matchers.map { HostGroupMatcher(it.tagName, it.tagValues) },
      policyArn = expectedIamPolicy.arn,
      policyVersionId = expectedIamPolicy.versionId
    )

    given(tenantService.getCredentials(safeEq(validHostGroup.tenantId)))
      .willReturn(awsCredentials)
    given(policyManager.create(safeEq(awsCredentials), safeEq(validHostGroup.name), safeEq(expectedIamPolicy.document)))
      .willReturn(expectedIamPolicy)
    given(hostGroupMapper.toHostGroup(validInputDto, expectedIamPolicy))
      .willReturn(expectedHostGroup)
    given(hostGroupMapper.toHostGroup(validInputDto))
      .willReturn(expectedHostGroup)
    given(hostGroupRepository.save(any(HostGroup::class.java)))
      .willReturn(expectedHostGroup)

    Assertions.assertEquals(hostGroupService.create(validInputDto), expectedId)
  }
}

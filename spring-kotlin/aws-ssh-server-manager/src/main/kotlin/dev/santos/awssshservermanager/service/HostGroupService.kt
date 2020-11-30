package dev.santos.awssshservermanager.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.dto.HostGroupMatcherDto
import dev.santos.awssshservermanager.exception.DuplicateHostGroupException
import dev.santos.awssshservermanager.exception.HostGroupTenantNotFoundException
import dev.santos.awssshservermanager.exception.TenantNotFoundException
import dev.santos.awssshservermanager.helper.IoHelper
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.repository.HostGroupRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class HostGroupService(val hostGroupRepository: HostGroupRepository,
                       val tenantService: TenantService,
                       val policyManager: PolicyManager) {
    companion object {
        const val SSM_POLICY_TEMPLATE_PATH = "/service/hostgroup/ssmPolicyTemplate.json"
        const val SSM_POLICY_TEMPLATE_SID = "[POLICY_SID_STR]"
        const val SSM_POLICY_TEMPLATE_STRING_LIKE_CONDITION = "\"[STRING_LIKE_CONDITION_OBJ]\""
    }

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun create(createHostGroupDto: CreateHostGroupDto): Long {
        return try {
            val iamPolicy = policyManager.create(
                    tenantService.getCredentials(createHostGroupDto.tenantId),
                    createHostGroupDto.name,
                    generateDocument(createHostGroupDto.name, createHostGroupDto.matchers)
            )

            val hostGroup = createHostGroupDto.toHostGroup(iamPolicy.arn, iamPolicy.versionId)

            hostGroupRepository.save(hostGroup).id
        } catch (e: DataIntegrityViolationException) {
            when (val exceptionCause = e.cause) {
                is ConstraintViolationException -> {
                    val exceptionMessage = exceptionCause.message.orEmpty()
                    when (exceptionCause.constraintName) {
                        "fk_tenant_id" -> throw HostGroupTenantNotFoundException(exceptionMessage)
                        "unique_tenantid_hostgroupname" -> throw DuplicateHostGroupException(exceptionMessage)
                    }
                }
            }
            throw e
        } catch (e: TenantNotFoundException) {
            throw HostGroupTenantNotFoundException(e.message)
        }
    }

    fun generateDocument(name: String, matchers: List<HostGroupMatcherDto>): String {
        val mapper = jacksonObjectMapper()
        val conditionalTags = matchers
                .map { "aws:RequestTag/${it.tagName}" to it.tagValues }
                .toMap()

        return IoHelper.readAsString(SSM_POLICY_TEMPLATE_PATH)
                .replace(SSM_POLICY_TEMPLATE_SID, name)
                .replace(SSM_POLICY_TEMPLATE_STRING_LIKE_CONDITION, mapper.writeValueAsString(conditionalTags))
    }
}
package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.exception.DuplicateHostGroupException
import dev.santos.awssshservermanager.exception.HostGroupTenantNotFoundException
import dev.santos.awssshservermanager.repository.HostGroupRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class HostGroupService(val hostGroupRepository: HostGroupRepository) {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun create(createHostGroupDto: CreateHostGroupDto): Long {
        val hostGroup = createHostGroupDto.toHostGroup()

        return try {
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
        }
    }
}
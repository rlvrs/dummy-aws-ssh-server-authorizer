package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreatePermissionDto
import dev.santos.awssshservermanager.exception.DuplicatePermissionException
import dev.santos.awssshservermanager.exception.PermissionGrantorNotFoundException
import dev.santos.awssshservermanager.exception.PermissionHostGroupNotFoundException
import dev.santos.awssshservermanager.exception.PermissionTenantNotFoundException
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import dev.santos.awssshservermanager.mapper.PermissionMapper
import dev.santos.awssshservermanager.model.Permission
import dev.santos.awssshservermanager.repository.PermissionRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class PermissionService(
  val policyManager: PolicyManager,
  val tenantService: TenantService,
  val hostGroupService: HostGroupService,
  val permissionRepository: PermissionRepository,
  val permissionMapper: PermissionMapper
) {
  fun create(createPermissionDto: CreatePermissionDto): Long {
    val permission = permissionMapper.toPermission(createPermissionDto)

    return try {
      attachUserPolicy(permission)
      permissionRepository.save(permission).id
    } catch (e: DataIntegrityViolationException) {
      when (val exceptionCause = e.cause) {
        is ConstraintViolationException -> {
          val exceptionMessage = exceptionCause.message.orEmpty()
          when (exceptionCause.constraintName) {
            "fk_tenant_id" -> throw PermissionTenantNotFoundException(exceptionMessage)
            "fk_grantor_id" -> throw PermissionGrantorNotFoundException(exceptionMessage)
            "fk_host_group_id" -> throw PermissionHostGroupNotFoundException(exceptionMessage)
            "unique_tenantid_grantee_hostgroup" -> throw DuplicatePermissionException(exceptionMessage)
          }
        }
      }
      throw e
    }
  }

  private fun attachUserPolicy(permission: Permission) {
    policyManager.attachUserPolicy(
      awsCredentials = tenantService.getCredentials(permission.tenantId),
      arn = hostGroupService.getHostGroupById(permission.hostGroupId).policyArn,
      userName = permission.grantee
    )
  }
}

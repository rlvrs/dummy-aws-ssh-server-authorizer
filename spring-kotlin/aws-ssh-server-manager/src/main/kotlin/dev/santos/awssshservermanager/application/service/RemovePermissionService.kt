package dev.santos.awssshservermanager.application.service

import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand
import dev.santos.awssshservermanager.application.port.input.RemovePermissionUseCase
import dev.santos.awssshservermanager.application.port.output.GetAwsCredentialsPort
import dev.santos.awssshservermanager.application.port.output.GetHostGroupPort
import dev.santos.awssshservermanager.application.port.output.RemovePermissionPort
import dev.santos.awssshservermanager.exception.HostGroupNotFoundException
import dev.santos.awssshservermanager.exception.PermissionNotFoundException
import dev.santos.awssshservermanager.exception.TenantNotFoundException
import dev.santos.awssshservermanager.lib.aws.exception.PolicyNotFoundException
import dev.santos.awssshservermanager.lib.aws.iam.PolicyManager
import org.springframework.stereotype.Service

@Service
class RemovePermissionService(
  val policyManager: PolicyManager,
  val getAwsCredentialsPort: GetAwsCredentialsPort,
  val getHostGroupPort: GetHostGroupPort,
  val removePermissionPort: RemovePermissionPort
) : RemovePermissionUseCase {
  @Throws(PermissionNotFoundException::class)
  override fun removePermission(removePermissionCommand: RemovePermissionCommand): Long {
    detachUserPolicy(removePermissionCommand)
    removePermissionPort.removePermission(removePermissionCommand.tenantId, removePermissionCommand.id)
    return removePermissionCommand.id
  }

  private fun detachUserPolicy(command: RemovePermissionCommand) {
    try {
      policyManager.detachUserPolicy(
        awsCredentials = getAwsCredentialsPort.getAwsCredentials(command.tenantId),
        arn = getHostGroupPort.getHostGroup(command.tenantId, command.hostGroupId).policyArn,
        userName = command.grantee
      )
    } catch (exception: Exception) {
      when (exception) {
        is PolicyNotFoundException,
        is TenantNotFoundException,
        is HostGroupNotFoundException -> throw PermissionNotFoundException(exception.message.orEmpty())
        else -> throw exception
      }
    }
  }
}

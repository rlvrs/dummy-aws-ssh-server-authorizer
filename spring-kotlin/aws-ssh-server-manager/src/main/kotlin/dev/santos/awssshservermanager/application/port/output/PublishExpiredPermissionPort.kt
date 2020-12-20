package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.application.port.input.RemovePermissionCommand

data class ExpiredPermissionEvent(
  val removePermissionCommand: RemovePermissionCommand
)

interface PublishExpiredPermissionPort {
  fun publish(event: ExpiredPermissionEvent)
}

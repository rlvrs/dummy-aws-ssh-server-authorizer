package dev.santos.awssshservermanager.adapter.listener

import dev.santos.awssshservermanager.application.port.input.RemovePermissionUseCase
import dev.santos.awssshservermanager.application.port.output.ExpiredPermissionEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ExpiredPermissionListener(
  val removePermissionUseCase: RemovePermissionUseCase
) {
  @Async
  @EventListener
  fun handleExpiredPermissionEvent(event: ExpiredPermissionEvent) {
    removePermissionUseCase.removePermission(event.removePermissionCommand)
  }
}

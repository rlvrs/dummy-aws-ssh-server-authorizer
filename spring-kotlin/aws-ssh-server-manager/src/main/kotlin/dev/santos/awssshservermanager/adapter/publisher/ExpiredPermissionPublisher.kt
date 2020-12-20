package dev.santos.awssshservermanager.adapter.publisher

import dev.santos.awssshservermanager.application.port.output.ExpiredPermissionEvent
import dev.santos.awssshservermanager.application.port.output.PublishExpiredPermissionPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ExpiredPermissionPublisher(
  val publisher: ApplicationEventPublisher
) : PublishExpiredPermissionPort {
  override fun publish(event: ExpiredPermissionEvent) {
    publisher.publishEvent(event)
  }
}

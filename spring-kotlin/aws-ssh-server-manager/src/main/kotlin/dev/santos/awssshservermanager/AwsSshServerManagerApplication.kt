package dev.santos.awssshservermanager

import dev.santos.awssshservermanager.application.service.RemoveExpiredPermissionsJob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableAsync
class AwsSshServerManagerApplication {
  @Autowired
  private lateinit var removeExpiredPermissionsJob: RemoveExpiredPermissionsJob

  @PostConstruct
  fun scheduleRecurrently() {
    removeExpiredPermissionsJob.run()
  }
}

fun main(args: Array<String>) {
  runApplication<AwsSshServerManagerApplication>(*args)
}

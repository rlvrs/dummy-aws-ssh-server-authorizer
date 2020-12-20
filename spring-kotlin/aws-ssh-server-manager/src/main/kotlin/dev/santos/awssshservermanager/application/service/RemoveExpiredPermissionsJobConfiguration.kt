package dev.santos.awssshservermanager.application.service

import org.jobrunr.scheduling.cron.CronExpression
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

data class RemoveExpiredPermissionsJobConfig(
  val cron: String
)

@Configuration
class RemoveExpiredPermissionsJobConfiguration {
  @Value("\${background-job.remove-expired-permissions.cron}")
  private val cronInput: String? = null

  @Bean
  fun removeExpiredPermissionsJobConfig(): RemoveExpiredPermissionsJobConfig {
    val cron = cronInput ?: throw IllegalArgumentException("[cron] expected for remove-expired-permissions job")
    validateCronExpression(cron)
    return RemoveExpiredPermissionsJobConfig(cron)
  }

  private fun validateCronExpression(cron: String) {
    CronExpression.create(cron)
  }
}

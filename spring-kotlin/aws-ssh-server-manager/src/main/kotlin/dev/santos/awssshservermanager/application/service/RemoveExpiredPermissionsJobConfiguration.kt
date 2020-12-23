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
  @Value("\${background-job.remove-expired-permissions.cron:#{null}}")
  private val cronInput: String? = null

  @Value("\${org.jobrunr.background-job-server.enabled}")
  private val backgroundJobServerEnabled: Boolean? = null

  @Bean
  fun removeExpiredPermissionsJobConfig(): RemoveExpiredPermissionsJobConfig {
    return when (backgroundJobServerEnabled) {
      null -> throw IllegalArgumentException("[org.jobrunr.background-job-server.enabled] is mandatory")
      true -> {
        val cron = cronInput ?: throw IllegalArgumentException("[cron] expected for remove-expired-permissions job")
        validateCronExpression(cron)
        RemoveExpiredPermissionsJobConfig(cron)
      }
      else -> RemoveExpiredPermissionsJobConfig("")
    }
  }

  private fun validateCronExpression(cron: String) {
    CronExpression.create(cron)
  }
}

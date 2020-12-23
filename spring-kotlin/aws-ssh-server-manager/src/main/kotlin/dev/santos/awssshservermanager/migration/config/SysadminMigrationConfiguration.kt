package dev.santos.awssshservermanager.migration.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

data class SysadminMigrationConfig(
  val enabled: Boolean,
  val password: String
)

@Configuration
class SysadminMigrationConfiguration {
  @Value("\${migration.insert-sysadmin.enabled:#{null}}")
  private val enabled: Boolean? = null

  @Value("\${migration.insert-sysadmin.password:#{null}}")
  private val sysadminPass: String? = null

  @Bean
  fun sysadminMigrationConfig(): SysadminMigrationConfig {
    return SysadminMigrationConfig(
      enabled = enabled ?: throw IllegalArgumentException("[migration.insert-sysadmin.enabled] is mandatory"),
      password = sysadminPass ?: throw IllegalArgumentException("[migration.insert-sysadmin.password] is mandatory")
    )
  }
}

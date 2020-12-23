package dev.santos.awssshservermanager.migration.config.springliquibase

import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(LiquibaseProperties::class)
class LiquibaseConfig(val dataSource: DataSource, val properties: LiquibaseProperties) {
  @Bean
  fun liquibase(): SpringLiquibase {
    val liquibase: SpringLiquibase = BeanAwareSpringLiquibase()
    liquibase.dataSource = dataSource
    liquibase.changeLog = properties.changeLog
    liquibase.contexts = properties.contexts
    liquibase.defaultSchema = properties.defaultSchema
    liquibase.isDropFirst = properties.isDropFirst
    liquibase.setShouldRun(properties.isEnabled)
    liquibase.labels = properties.labels
    liquibase.setChangeLogParameters(properties.parameters)
    liquibase.setRollbackFile(properties.rollbackFile)
    return liquibase
  }
}

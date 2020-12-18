package dev.santos.awssshservermanager.helper

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

internal class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image)
interface PgsqlContainer {
  companion object {
    private val container = KPostgreSQLContainer("postgres:13.0").apply {
      withDatabaseName("testdb")
      withUsername("postgres")
      withPassword("123")
      withInitScript("containers/init_pgsql.sql")
      start()
    }

    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      registry.add("spring.liquibase.url", container::getJdbcUrl)
      registry.add("spring.liquibase.user", container::getUsername)
      registry.add("spring.liquibase.password", container::getPassword)

      registry.add("spring.datasource.url", container::getJdbcUrl)
      registry.add("spring.datasource.username") { "myuser" }
      registry.add("spring.datasource.password") { "mypass" }
    }
  }
}

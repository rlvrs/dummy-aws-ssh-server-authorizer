package dev.santos.awssshservermanager.migration

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.CreateUserDtoRole
import dev.santos.awssshservermanager.migration.config.SysadminMigrationConfig
import dev.santos.awssshservermanager.migration.config.springliquibase.BeanAwareSpringLiquibase.Companion.getBean
import dev.santos.awssshservermanager.service.TenantService
import dev.santos.awssshservermanager.service.UserService
import liquibase.Scope
import liquibase.change.custom.CustomTaskChange
import liquibase.change.custom.CustomTaskRollback
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import liquibase.exception.RollbackImpossibleException
import liquibase.exception.SetupException
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

class SysadminMigration : CustomTaskChange, CustomTaskRollback {
  private lateinit var sysadminMigrationConfig: SysadminMigrationConfig
  private lateinit var userService: UserService
  private lateinit var tenantService: TenantService
  private lateinit var resourceAccessor: ResourceAccessor

  private var newTenantId: Long? = null
  private var newUserId: Long? = null

  @Throws(CustomChangeException::class)
  override fun execute(database: Database) {
    if (!sysadminMigrationConfig.enabled) {
      Scope.getCurrentScope().getLog(javaClass).info("[Disabled] Ignoring Sysadmin tenant and user...")
      return
    } else {
      Scope.getCurrentScope().getLog(javaClass).info("Inserting Sysadmin tenant and user...")
    }

    newTenantId = tenantService.create(
      CreateTenantRequest(
        name = "sysadmin-tenant",
        awsApiKey = "not-relevant",
        awsApiSecret = "not-relevant"
      )
    )

    newUserId = userService.create(
      CreateUserDto(
        awsUsername = "sysadmin",
        firstName = "System",
        lastName = "Administrator",
        password = sysadminMigrationConfig.password,
        tenantId = newTenantId!!,
        role = CreateUserDtoRole.SYSADMIN
      )
    )
  }

  @Throws(CustomChangeException::class, RollbackImpossibleException::class)
  override fun rollback(database: Database) {
  }

  override fun getConfirmationMessage(): String {
    return "Inserted Sysadmin tenant and user..."
  }

  @Throws(SetupException::class)
  override fun setUp() {
    userService = getBean(UserService::class.java)
    tenantService = getBean(TenantService::class.java)
    sysadminMigrationConfig = getBean(SysadminMigrationConfig::class.java)
  }

  override fun setFileOpener(resourceAccessor: ResourceAccessor) {
    this.resourceAccessor = resourceAccessor
  }

  override fun validate(database: Database): ValidationErrors {
    return ValidationErrors()
  }
}

package dev.santos.awssshservermanager.migration.config.springliquibase

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ResourceLoader

class BeanAwareSpringLiquibase : SpringLiquibase() {
  override fun setResourceLoader(resourceLoader: ResourceLoader) {
    super.setResourceLoader(resourceLoader)
    applicationContext = resourceLoader
  }

  companion object {
    lateinit var applicationContext: ResourceLoader

    @Throws(Exception::class)
    fun <T> getBean(beanClass: Class<T>): T {
      return if (ApplicationContext::class.java.isInstance(applicationContext)) {
        (applicationContext as ApplicationContext).getBean(beanClass)
      } else {
        throw Exception("Resource loader is not an instance of ApplicationContext")
      }
    }
  }
}

package dev.santos.awssshservermanager.adapter.persistence

import dev.santos.awssshservermanager.application.port.output.GetAwsCredentialsPort
import dev.santos.awssshservermanager.exception.TenantNotFoundException
import dev.santos.awssshservermanager.repository.TenantRepository
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials

@Component
class TenantPersistenceAdapter(
  private val tenantRepository: TenantRepository
) : GetAwsCredentialsPort {
  override fun getAwsCredentials(tenantId: Long): AwsCredentials {
    return tenantRepository.findById(tenantId)
      .map {
        return@map AwsBasicCredentials.create(it.awsApiKey, it.awsApiSecret)
      }.orElseThrow {
        TenantNotFoundException("Tenant id [${tenantId}] not found!")
      }
  }
}

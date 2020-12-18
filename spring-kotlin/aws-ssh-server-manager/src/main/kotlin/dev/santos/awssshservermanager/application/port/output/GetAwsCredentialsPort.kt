package dev.santos.awssshservermanager.application.port.output

import dev.santos.awssshservermanager.exception.TenantNotFoundException
import software.amazon.awssdk.auth.credentials.AwsCredentials

interface GetAwsCredentialsPort {
  @Throws(TenantNotFoundException::class)
  fun getAwsCredentials(tenantId: Long): AwsCredentials
}

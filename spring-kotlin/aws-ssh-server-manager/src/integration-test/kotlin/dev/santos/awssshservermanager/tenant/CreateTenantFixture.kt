package dev.santos.awssshservermanager.tenant

import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.dto.CreateTenantResponse
import dev.santos.awssshservermanager.helper.lib.aws.iam.AwsAccessKey
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.repository.TenantRepository
import org.springframework.boot.test.context.TestComponent
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@TestComponent
class CreateTenantFixture(
  val tenantRepository: TenantRepository,
  val mockMvc: MockMvc
) {
  val tenantName = "create-tenant-company"

  fun createTenant(tenantAwsAccessKey: AwsAccessKey) {
    if (tenantRepository.findByName(tenantName) != null) {
      return
    }

    val expectedId = 1L
    val request = CreateTenantRequest(
      name = tenantName,
      awsApiKey = tenantAwsAccessKey.accessKeyId,
      awsApiSecret = tenantAwsAccessKey.secretAccessKey
    )

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = objToJsonStr(request)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(objToJsonStr(CreateTenantResponse(expectedId))) }
    }
  }
}

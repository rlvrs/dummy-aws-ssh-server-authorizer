package dev.santos.awssshservermanager.controller

import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateTenantRequest
import dev.santos.awssshservermanager.dto.CreateTenantResponse
import dev.santos.awssshservermanager.exception.DuplicateTenantException
import dev.santos.awssshservermanager.helper.objToJsonStr
import dev.santos.awssshservermanager.helper.strToJsonObj
import dev.santos.awssshservermanager.service.TenantService
import org.hamcrest.Matchers
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TenantController::class])
class TenantControllerShould {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var tenantService: TenantService

  private val validCreateTenantRequest = CreateTenantRequest(
    name = "some-company",
    awsApiKey = "super_secret_key",
    awsApiSecret = "super_secret_secret"
  )

  @Test
  fun `return HTTP 201 when the tenant is created`() {
    BDDMockito.given(this.tenantService.create(validCreateTenantRequest))
      .willReturn(1)

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(validCreateTenantRequest)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(objToJsonStr(CreateTenantResponse(1L))) }
      content { jsonPath<Int>("$.id", Matchers.`is`(1)) }
    }
  }

  @Test
  fun `return 409 when the tenant exists`() {
    val testTenantRequest = CreateTenantRequest(
      name = "some-company",
      awsApiKey = "super_secret_key",
      awsApiSecret = "super_secret_secret"
    )
    BDDMockito.willThrow(DuplicateTenantException("Tenant -1 already exists!"))
      .given(this.tenantService).create(testTenantRequest)

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(testTenantRequest)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isConflict() }
    }
  }

  @Test
  fun `return 400 when the payload is empty`() {
    BDDMockito.given(tenantService.create(strToJsonObj("")))
      .willReturn(1)

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = ""
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isBadRequest() }
    }
  }

  @TestFactory
  fun `return 400 when the payload is invalid`() = listOf(
    Pair("name smaller than 2 chars", validCreateTenantRequest.copy(name = "j")),
    Pair("name bigger than 50 chars", validCreateTenantRequest.copy(name = "a".repeat(251))),
    Pair("name starts with number", validCreateTenantRequest.copy(name = "1johndoe")),
    Pair("name starts with underscore", validCreateTenantRequest.copy(name = "_johndoe")),
    Pair("name starts with hyphen", validCreateTenantRequest.copy(name = "-johndoe")),
    Pair("name ends with hyphen", validCreateTenantRequest.copy(name = "john-doe-")),
    Pair("name has a symbol", validCreateTenantRequest.copy(name = "jo\$hndoe")),
    Pair("name has a symbol", validCreateTenantRequest.copy(name = "johndoe.")),
    Pair("null name", validCreateTenantRequest.copy(name = null)),
    Pair("null awsApiKey", validCreateTenantRequest.copy(awsApiKey = null)),
    Pair("null awsApiSecret", validCreateTenantRequest.copy(awsApiSecret = null)),
  ).map { (testName: String, createTenantRequest: CreateTenantRequest) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito.given(tenantService.create(createTenantRequest))
        .willReturn(1)

      mockMvc.post("/tenant") {
        contentType = MediaType.APPLICATION_JSON
        content = objToJsonStr(createTenantRequest)
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isBadRequest() }
      }
    }
  }
}

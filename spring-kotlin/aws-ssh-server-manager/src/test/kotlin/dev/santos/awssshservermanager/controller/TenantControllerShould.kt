package dev.santos.awssshservermanager.controller

import com.google.gson.Gson
import dev.santos.awssshservermanager.dto.CreateTenantDto
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TenantController::class])
class TenantControllerShould {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var tenantService: TenantService

  @Test
  fun `return HTTP 201 when the tenant is created`() {
    val testTenantDto = CreateTenantDto(
      name = "some-company",
      awsApiKey = "super_secret_key",
      awsApiSecret = "super_secret_secret"
    )

    BDDMockito.given(this.tenantService.create(testTenantDto))
      .willReturn(1)

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(testTenantDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isCreated() }
      content { MockMvcResultMatchers.status().isBadRequest }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json("{\"id\":1}") }
      content { jsonPath<Int>("$.id", Matchers.`is`(1)) }
    }
  }

  @Test
  fun `return 409 when the tenant exists`() {
    val testTenantDto = CreateTenantDto(
      name = "some-company",
      awsApiKey = "super_secret_key",
      awsApiSecret = "super_secret_secret"
    )
    BDDMockito.given(this.tenantService.create(testTenantDto))
      .willAnswer {
        throw DuplicateTenantException("Tenant -1 already exists!")
      }

    mockMvc.post("/tenant") {
      contentType = MediaType.APPLICATION_JSON
      content = Gson().toJson(testTenantDto)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isConflict() }
    }
  }

  @TestFactory
  fun `return 400 when the payload is invalid`() = listOf(
    Pair("name smaller than 2 chars", objToJsonStr(CreateTenantDto(name = "j"))),
    Pair("name bigger than 50 chars", objToJsonStr(CreateTenantDto(name = "A".repeat(51)))),
    Pair("name starts with number", objToJsonStr(CreateTenantDto(name = "1johndoe"))),
    Pair("name starts with underscore", objToJsonStr(CreateTenantDto(name = "_johndoe"))),
    Pair("name starts with hyphen", objToJsonStr(CreateTenantDto(name = "-johndoe"))),
    Pair("name ends with hyphen", objToJsonStr(CreateTenantDto(name = "john-doe-"))),
    Pair("name has a symbol", objToJsonStr(CreateTenantDto(name = "jo\$hndoe"))),
    Pair("name has a symbol", objToJsonStr(CreateTenantDto(name = "johndoe."))),
    Pair("empty request body", ""),
    Pair("null name", "{\"name\":null,\"awsApiKey\":\"\",\"awsApiSecret\":\"\"}"),
    Pair("null awsApiKey", "{\"name\":\"johndoe\",\"awsApiKey\":null,\"awsApiSecret\":\"\"}"),
    Pair("null awsApiSecret", "{\"name\":\"johndoe\",\"awsApiKey\":\"\",\"awsApiSecret\":null}"),
  ).map { (testName: String, createTenantDtoStr: String) ->
    DynamicTest.dynamicTest(testName) {
      BDDMockito.given(tenantService.create(strToJsonObj(createTenantDtoStr)))
        .willReturn(1)

      mockMvc.post("/tenant") {
        contentType = MediaType.APPLICATION_JSON
        content = createTenantDtoStr
        accept = MediaType.APPLICATION_JSON
      }.andExpect {
        status { isBadRequest() }
      }
    }
  }
}

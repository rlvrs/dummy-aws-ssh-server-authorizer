package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.dto.CreateUserDtoRole
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.exception.UserTenantNotFoundException
import dev.santos.awssshservermanager.mapper.UserMapper
import dev.santos.awssshservermanager.model.User
import dev.santos.awssshservermanager.repository.UserRepository
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.sql.SQLException

@SpringBootTest
@ExtendWith(SpringExtension::class)
class UserServiceShould {
  @Mock
  private lateinit var userRepository: UserRepository

  @Mock
  private lateinit var userMapper: UserMapper

  @InjectMocks
  private lateinit var userService: UserService

  private val validUserDto = CreateUserDto(
    awsUsername = "john.doe",
    firstName = "John",
    lastName = "Doe",
    password = "str0ngP455!",
    tenantId = 1L,
    role = CreateUserDtoRole.ADMIN
  )

  @Test
  fun `create a user successfully`() {
    val expectedId: Long = 2
    val expectedUser = User(
      id = expectedId
    )

    given(userRepository.save(any(User::class.java)))
      .willReturn(expectedUser)
    given(userMapper.toUser(validUserDto))
      .willReturn(expectedUser)

    Assertions.assertEquals(userService.create(validUserDto), expectedId)
  }

  @Test
  fun `throw an exception when the user exists in the DB`() {
    doThrow(
      DataIntegrityViolationException(
        "Tenant -1 already exists!", ConstraintViolationException("", SQLException(), "unique_tenantid_userawsusername")
      )
    ).`when`(userRepository).save(any())

    Assertions.assertThrows(DuplicateUserException::class.java) {
      userService.create(validUserDto)
    }
  }

  @Test
  fun `throw an exception when the user tenant is not found`() {
    doThrow(
      DataIntegrityViolationException(
        "Tenant -1 not found!", ConstraintViolationException("", SQLException(), "fk_tenant_id")
      )
    ).`when`(userRepository).save(any())

    Assertions.assertThrows(UserTenantNotFoundException::class.java) {
      userService.create(validUserDto)
    }
  }
}

package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.exception.UserTenantNotFoundException
import dev.santos.awssshservermanager.mapper.UserMapper
import dev.santos.awssshservermanager.repository.UserRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class UserService(
  val userRepository: UserRepository,
  val userMapper: UserMapper
) {
  fun create(createUserDto: CreateUserDto): Long {
    val encryptedUser = userMapper.toUser(createUserDto)

    return try {
      userRepository.save(encryptedUser).id
    } catch (e: DataIntegrityViolationException) {
      when (val exceptionCause = e.cause) {
        is ConstraintViolationException -> {
          val exceptionMessage = exceptionCause.message.orEmpty()
          when (exceptionCause.constraintName) {
            "fk_tenant_id" -> throw UserTenantNotFoundException(exceptionMessage)
            "unique_tenantid_userawsusername" -> throw DuplicateUserException(exceptionMessage)
          }
        }
      }
      throw e
    }
  }
}

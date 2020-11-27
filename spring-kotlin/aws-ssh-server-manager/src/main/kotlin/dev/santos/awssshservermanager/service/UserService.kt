package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.exception.UserTenantNotFoundException
import dev.santos.awssshservermanager.repository.UserRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun create(createUserDto: CreateUserDto): Long {
        val encryptedUser = createUserDto.toUserAdmin(passwordEncoder.encode(createUserDto.password))

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
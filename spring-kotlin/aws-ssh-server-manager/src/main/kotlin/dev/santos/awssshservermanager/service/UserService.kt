package dev.santos.awssshservermanager.service

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.exception.DuplicateUserException
import dev.santos.awssshservermanager.model.User
import dev.santos.awssshservermanager.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun create(createUserDto: CreateUserDto): Long {
        val encryptedUser = User(
            username = createUserDto.username,
            awsUsername = createUserDto.awsUsername,
            password = passwordEncoder.encode(createUserDto.password)
        )

        return try {
            userRepository.save(encryptedUser).id
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateUserException(e.message!!)
        }
    }
}
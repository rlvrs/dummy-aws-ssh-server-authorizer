package dev.santos.awssshservermanager

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class AwsSshServerManagerApplicationTests {
	@MockBean private lateinit var passwordEncoder: PasswordEncoder

	@Test
	fun contextLoads() {
	}

}

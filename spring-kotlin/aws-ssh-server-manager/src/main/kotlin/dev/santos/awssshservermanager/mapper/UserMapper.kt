package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateUserDto
import dev.santos.awssshservermanager.model.User
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Qualifier
import org.mapstruct.ReportingPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Qualifier
@Target(AnnotationTarget.FUNCTION)
annotation class EncodedMapping

@Component
class PasswordEncoderMapper(private val passwordEncoder: PasswordEncoder) {
  @EncodedMapping
  fun encode(value: String): String {
    return passwordEncoder.encode(value)
  }
}

@Mapper(
  componentModel = "spring",
  uses = [PasswordEncoderMapper::class],
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {
  @Mappings(
    Mapping(source = "tenantId", target = "tenantId"),
    Mapping(source = "awsUsername", target = "awsUsername"),
    Mapping(source = "firstName", target = "firstName"),
    Mapping(source = "lastName", target = "lastName"),
    Mapping(target = "password", qualifiedBy = [EncodedMapping::class]),
    Mapping(source = "role", target = "role")
  )
  fun toUser(createTenantDto: CreateUserDto): User
}

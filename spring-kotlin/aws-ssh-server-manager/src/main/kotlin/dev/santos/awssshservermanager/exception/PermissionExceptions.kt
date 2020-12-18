package dev.santos.awssshservermanager.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class DuplicatePermissionException(override val message: String) : RuntimeException()

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class PermissionTenantNotFoundException(override val message: String) : RuntimeException()

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class PermissionGrantorNotFoundException(override val message: String) : RuntimeException()

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class PermissionHostGroupNotFoundException(override val message: String) : RuntimeException()

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class PermissionNotFoundException(override val message: String) : RuntimeException()

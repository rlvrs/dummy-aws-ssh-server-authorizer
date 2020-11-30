package dev.santos.awssshservermanager.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class DuplicateHostGroupException(override val message: String) : RuntimeException()
@ResponseStatus(code = HttpStatus.NOT_FOUND)
class HostGroupTenantNotFoundException(override val message: String) : RuntimeException()
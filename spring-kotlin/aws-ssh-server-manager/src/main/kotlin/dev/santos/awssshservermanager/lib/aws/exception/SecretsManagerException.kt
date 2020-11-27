package dev.santos.awssshservermanager.lib.aws.exception

class DuplicateSecretException(override val message: String): Exception()
class SecretNotFoundException(override val message: String): Exception()
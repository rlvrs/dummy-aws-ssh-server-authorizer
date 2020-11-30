package dev.santos.awssshservermanager.lib.aws.exception

class DuplicatePolicyException(override val message: String): Exception()
class PolicyNotFoundException(override val message: String): Exception()
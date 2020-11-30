package dev.santos.awssshservermanager.lib.aws.model

data class IamPolicy(
        val document: String,
        val versionId: String,
        val arn: String
)
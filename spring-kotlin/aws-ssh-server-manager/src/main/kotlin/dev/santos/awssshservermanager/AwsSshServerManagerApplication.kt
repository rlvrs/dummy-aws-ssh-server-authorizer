package dev.santos.awssshservermanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AwsSshServerManagerApplication

fun main(args: Array<String>) {
  runApplication<AwsSshServerManagerApplication>(*args)
}

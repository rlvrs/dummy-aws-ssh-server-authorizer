package dev.santos.awssshservermanager.helper

object ResourceHelper {
  fun readAsString(resourcePath: String): String {
    return ResourceHelper::class.java.getResource(resourcePath).readText()
  }
}

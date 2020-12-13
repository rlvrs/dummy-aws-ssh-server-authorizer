package dev.santos.awssshservermanager.helper

object IoHelper {
  fun readAsString(resourcePath: String): String {
    return IoHelper::class.java.getResource(resourcePath).readText()
  }
}

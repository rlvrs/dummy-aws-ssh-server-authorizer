package dev.santos.awssshservermanager.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson

fun objToJsonStr(obj: Any): String = Gson().toJson(obj)

inline fun <reified T> strToJsonObj(objStr: String): T = Gson().fromJson(objStr, T::class.java)

fun minifyJsonStr(json: String): String = ObjectMapper().readValue(json, JsonNode::class.java).toString()

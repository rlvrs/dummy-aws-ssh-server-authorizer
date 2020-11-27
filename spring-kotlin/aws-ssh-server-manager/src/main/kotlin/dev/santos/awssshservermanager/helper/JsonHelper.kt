package dev.santos.awssshservermanager.helper

import com.google.gson.Gson

fun objToJsonStr(obj: Any): String = Gson().toJson(obj)

inline fun <reified T> strToJsonObj(objStr: String): T = Gson().fromJson(objStr, T::class.java)
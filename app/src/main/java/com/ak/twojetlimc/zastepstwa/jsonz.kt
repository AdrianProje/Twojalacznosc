package com.ak.twojetlimc.zastepstwa

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement


//Zapisz/odczytaj zastepstwo

fun serializeZastepstwo(item: Zastepstwo): String {
    return Json.encodeToJsonElement(item).toString()
}

fun deserializeZastepstwo(json: String): Zastepstwo {
    return Json.decodeFromString(json)
}
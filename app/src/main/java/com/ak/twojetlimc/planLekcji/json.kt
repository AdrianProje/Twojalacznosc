package com.ak.twojetlimc.planLekcji

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

//Zapisz/odczytaj schedule

fun serializeSchedule(item: Schedule): String {
    return Json.encodeToJsonElement(item).toString()
}

fun deserializeSchedule(json: String): Schedule {
    return Json.decodeFromString(json)
}
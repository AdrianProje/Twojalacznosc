package com.ak.twojetlimc.planLekcji

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

fun serializenewSchedule(item: List<Schedule>): String {
    return Json.encodeToJsonElement(item).toString()
}

fun deserializenewSchedule(json: String): List<Schedule> {
    return Json.decodeFromString(json)
}


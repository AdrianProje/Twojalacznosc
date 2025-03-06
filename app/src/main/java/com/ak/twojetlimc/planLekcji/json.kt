package com.ak.twojetlimc.planLekcji

import com.google.gson.Gson

val gson = Gson()

//Zapisz/odczytaj schedule

fun serializeSchedule(item: Schedule): String {
    return gson.toJson(item)
}

fun deserializeSchedule(json: String): Schedule {
    return gson.fromJson(json, Schedule::class.java)
}
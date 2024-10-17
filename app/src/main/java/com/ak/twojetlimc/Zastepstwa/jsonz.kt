package com.ak.twojetlimc.Zastepstwa

import com.google.gson.Gson

val gson = Gson()

//Zapisz/odczytaj zastepstwo

fun serializeZastepstwo(item: Zastepstwo): String {
    return gson.toJson(item)
}

fun deserializeZastepstwo(json: String): Zastepstwo {
    return gson.fromJson(json, Zastepstwo::class.java)
}
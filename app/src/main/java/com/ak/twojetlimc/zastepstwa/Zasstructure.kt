package com.ak.twojetlimc.zastepstwa

import kotlinx.serialization.Serializable

//Jedno zastepstwo
@Serializable
data class Zastepstwo(
    val numerLekcji: Int,
    val klasa: String,
    val zastepca: String,
    val uwagi: String
)
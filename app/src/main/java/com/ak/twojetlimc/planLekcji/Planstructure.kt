package com.ak.twojetlimc.planLekcji

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val imieinazwisko: String,
    val html: String,
    val plan: List<ScheduleItem>
)

@Serializable
//Jedna lekcja z planu
data class ScheduleItem(
    val numerLekcji: Int,
    val czas: String,
    val dzien: Int,
    val nauczyciel: String,
    val klasa: String,
    val przedmiot: String,
    val sala: String
)


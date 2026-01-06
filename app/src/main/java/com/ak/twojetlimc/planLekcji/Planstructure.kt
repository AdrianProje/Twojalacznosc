package com.ak.twojetlimc.planLekcji

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val imieinazwisko: String,
    val html: String,
    val plan: List<ScheduleItem>
)

@Serializable
data class ScheduleItemDetails(
    val nauczyciel: String,
    val nauczyciellink: String,
    val przedmiot: String,
    val klasalink: String,
    val sala: String,
    val salalink: String
)

@Serializable
//Jedna lekcja z planu
data class ScheduleItem(
    val numerLekcji: Int,
    val czas: String,
    val dzien: Int,
    val klasa: String,
    val detale: List<ScheduleItemDetails>
)


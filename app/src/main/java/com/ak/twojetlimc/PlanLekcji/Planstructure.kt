package com.ak.twojetlimc.PlanLekcji

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

data class Schedule(
    val imieinazwisko: String,
    val html: String,
    val plan: List<ScheduleItem>
)
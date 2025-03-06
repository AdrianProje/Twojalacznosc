package com.ak.twojetlimc.zditm

import kotlinx.serialization.Serializable


@Serializable
data class Tablicaodjazow(
    val stop_name: String,
    val stop_number: String,
    val departures: List<Departure>,
    val message: String?,
    val updated_at: String
)


@Serializable
data class Departure(
    val line_number: String,
    val direction: String,
    val time_real: Int?,
    val time_scheduled: String?
)

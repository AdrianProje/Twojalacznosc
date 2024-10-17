package com.ak.twojetlimc.PlanLekcji

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ak.twojetlimc.komponenty.Datastoremanager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

data class ScheduleData(val imieinazwisko: String, val filePath: String, val htmlvalue: String)

@SuppressLint("SuspiciousIndentation")
@Composable
fun GetList(typ: Int): List<ScheduleData?> {
    val contextu = LocalContext.current
    val datastore = Datastoremanager(contextu)
    val list = mutableListOf<ScheduleData?>()
    val list2 = mutableListOf("o", "s", "n")
    var litera = ""

    val exampleData = runBlocking {
        datastore.getPlanTimestamp.first()
    }

    when (typ) {
        1 -> {
            litera = "o"
        }

        2 -> {
            litera = "s"
        }

        3 -> {
            litera = "n"
        }

        else -> {
            null
        }
    }


    runBlocking {
        if (litera != "") {
            for (i in 1..70) {
                Log.d(
                    "PlanList - Tworzenie listy",
                    "$exampleData/$litera$i"
                )
                try {
                    val data1 = datastore.getSchedule(contextu, "$exampleData/$litera$i", 1)
                    if (data1 != null) {
                        list.add(
                            ScheduleData(
                                data1.imieinazwisko,
                                "$exampleData/$litera$i",
                                "$litera$i"
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.d("PlanList -Datastore nie posiada takiego planu", e.message.toString())
                }

            }
        } else {
            for (litery in list2) {
                for (i in 1..70) {
                    Log.d(
                        "PlanList - Tworzenie listy",
                        "$exampleData/$litery$i"
                    )
                    try {
                        val data2 = datastore.getSchedule(contextu, "$exampleData/$litery$i", 1)
                        if (data2 != null) {
                            list.add(
                                ScheduleData(
                                    data2.imieinazwisko,
                                    "$exampleData/$litery$i",
                                    "$litery$i"
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("PlanList -Datastore nie posiada takiego planu", e.message.toString())
                    }
                }
            }
        }
    }
    return list
}
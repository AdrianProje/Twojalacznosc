package com.ak.twojetlimc.PlanLekcji

import android.content.Context
import android.util.Log
import com.ak.twojetlimc.komponenty.Datastoremanager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

data class ScheduleData(val imieinazwisko: String, val filePath: String, val htmlvalue: String)

fun GetList(typ: Int, context: Context): List<ScheduleData?> {
    val datastore = Datastoremanager(context)
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
                    val data1 = datastore.getSchedule(context, "$exampleData/$litera$i", 1)
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
                        val data2 = datastore.getSchedule(context, "$exampleData/$litery$i", 1)
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
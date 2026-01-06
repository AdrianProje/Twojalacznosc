package com.ak.twojetlimc.planLekcji

import android.content.Context
import com.ak.twojetlimc.komponenty.Datastoremanager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

data class ScheduleData(val imieinazwisko: String, val htmlvalue: String)

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
        val data = datastore.getnewSchedule(context, exampleData.toString(), 1)
        if (litera != "") {
            data!!.forEach { schedule ->
                if (schedule.html.contains(litera)) {
                    val scheduledata = ScheduleData(schedule.imieinazwisko, schedule.html)
                    list.add(scheduledata)
                }
            }
        } else {
            data!!.forEach { schedule ->
                val scheduledata = ScheduleData(schedule.imieinazwisko, schedule.html)
                list.add(scheduledata)
            }
        }
    }
    return list
}
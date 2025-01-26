package com.ak.twojetlimc.komponenty

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.MainScreen
import com.ak.twojetlimc.R
import com.ak.twojetlimc.Zastepstwa.Zastepstwo
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Calendar

class RefreshWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    object DataHolder {
        val workerResult = MutableLiveData<Int>()
    }


    override suspend fun doWork(): Result {
        val calendarminutes = Calendar.getInstance().get(Calendar.MINUTE)
        val itemId = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            7 -> 1

            8 -> if (calendarminutes < 30) {
                2
            } else {
                3
            }

            9 -> 4

            10 -> 5

            11 -> 6

            12 -> 7

            13 -> 8

            14 -> 9

            15 -> 10

            16 -> 11

            17 -> 12

            18 -> 13

            else -> 0
        }

        val accessdata = Datastoremanager(applicationContext)
        if (accessdata.getFavSchedule.first() != "") {
            val klasa = accessdata.getFavSchedule.first()!!.split(",")[0].substring(0, 3)
            val htmlvalue = accessdata.getFavSchedule.first()!!.split(",")[1]
            val timestamp = accessdata.getPlanTimestamp.first()


            Log.d("Plannotification", "Wywołanie funkcji $timestamp/$htmlvalue")
            accessdata.getSchedule(
                applicationContext,
                "$timestamp/$htmlvalue",
                1
            )!!.plan.forEach {
                if (itemId == it.numerLekcji && it.dzien + 1 == LocalDate.now().dayOfWeek.value && it.przedmiot != "") {
                    var listitems = mutableListOf<Zastepstwo>()
                    try {
                        val datastore = Datastoremanager(applicationContext)
                        val data1 = datastore.getZastepstwo(
                            applicationContext, it.numerLekcji, klasa,
                            LocalDate.now().toString(), 1
                        )
                        val data2 = datastore.getZastepstwo(
                            applicationContext, it.numerLekcji, klasa + "(1)",
                            LocalDate.now().toString(), 1
                        )
                        val data3 = datastore.getZastepstwo(
                            applicationContext, it.numerLekcji, klasa + "(2)",
                            LocalDate.now().toString(), 1
                        )
                        if (data1 != null) {
                            listitems.add(data1)
                        }
                        if (data2 != null) {
                            listitems.add(data2)
                        }
                        if (data3 != null) {
                            listitems.add(data3)
                        }
                    } catch (e: Exception) {
                        Log.d(
                            "Plannotification",
                            "Nie znaleziono zastępstwa dla danej lekcji"
                        )
                        null
                    }

                    if (listitems.isEmpty()) {
                        showNotification(
                            applicationContext,
                            "Następna lekcja: ${it.przedmiot}", if (it.sala != "") {
                                "Sala: ${it.sala}, "
                            } else {
                                ""
                            } + if (it.klasa != "") {
                                "Klasa: ${it.klasa}, "
                            } else {
                                ""
                            } + if (it.nauczyciel != "") {
                                "Nauczyciel: ${it.nauczyciel}, "
                            } else {
                                ""
                            } + "Godzina: ${it.czas}"
                        )
                        Log.d("Plannotification", "Wysyłanie powiadomienia!")
                        DataHolder.workerResult.postValue(it.numerLekcji)
                    } else {
                        var notificationTextklasa = ""
                        var notificationTextzastepca = ""
                        var notificationTextuwagi = ""
                        listitems.forEach { zastdata2 ->
                            notificationTextklasa += zastdata2.klasa + " -- "
                            notificationTextzastepca += zastdata2.zastepca + " -- "
                            notificationTextuwagi += zastdata2.uwagi + " -- "
                        }
                        showNotification(
                            applicationContext,
                            "Następna lekcja (zastępstwo): ${notificationTextklasa}",
                            notificationTextzastepca + " | " + notificationTextuwagi
                        )
                        Log.d("Plannotification", "Wysyłanie powiadomienia (zastępstwo)!")
                        DataHolder.workerResult.postValue(it.numerLekcji)
                    }
                } else {
                    Log.d("Plannotification", "Dane lekcji się nie zgadzają z porównywanymi danymi")
                }
            }
        }
        return Result.success()
    }

    private fun showNotification(applicationContext: Context, title: String, description: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an Intent for the activity you want to start.
        val notifyIntent = Intent(applicationContext, MainScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "plan")
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat.Builder(applicationContext, "PLAN")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.lacznosc_logo_full)
            .apply {
                setContentIntent(notifyPendingIntent)
            }

        notificationManager.notify(1, notificationBuilder.build())
    }
}
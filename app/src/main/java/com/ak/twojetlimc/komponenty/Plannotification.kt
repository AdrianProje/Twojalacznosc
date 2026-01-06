package com.ak.twojetlimc.komponenty

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.MainScreen
import com.ak.twojetlimc.R
import com.ak.twojetlimc.zastepstwa.Zastepstwo
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class RefreshWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val itemId = getcurrenthour()

        val accessdata = Datastoremanager(applicationContext)
        if (accessdata.getFavSchedule.first() != "") {
            val klasa = accessdata.getFavSchedule.first()!!.split(",")[0].substring(0, 3)
            val htmlvalue = accessdata.getFavSchedule.first()!!.split(",")[1]
            val timestamp = accessdata.getPlanTimestamp.first()
            val preferedgroup = accessdata.getPreferedGroup.first()
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            Log.d("Plannotification", "Wywołanie funkcji $timestamp/$htmlvalue")
            accessdata.getnewchosenClass(
                applicationContext,
                timestamp.toString(),
                htmlvalue,
                1
            )?.plan?.forEach {
                if (itemId == it.numerLekcji && it.dzien + 1 == LocalDate.now().dayOfWeek.value) {
                    val listitems = mutableListOf<Zastepstwo>()
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

                    var przedmiot = " "
                    var sala = ""
                    var nauczyciel = ""

                    if (it.detale.size == 1) {
                        when (preferedgroup) {
                            0 -> for (details in it.detale) {
                                przedmiot += details.przedmiot + " "
                                sala += details.sala + " "
                                nauczyciel += details.nauczyciel + " "
                            }

                            1 -> {
                                przedmiot += it.detale.first().przedmiot
                                sala += it.detale.first().sala
                                nauczyciel += it.detale.first().nauczyciel
                            }

                            2 -> {
                                przedmiot += it.detale.last().przedmiot
                                sala += it.detale.last().sala
                                nauczyciel += it.detale.last().nauczyciel
                            }
                        }
                    } else {
                        przedmiot += it.detale.first().przedmiot
                        sala += it.detale.first().sala
                        nauczyciel += it.detale.first().nauczyciel
                    }

                    //przedmiot, sala, nauczyciel, czas

                    showNotification(
                        applicationContext,
                        notificationManager,
                        "Następna lekcja: $przedmiot",
                        " Sala: $sala | Nauczyciel: $nauczyciel | godzina: ${it.czas}"
                    )
                    Log.d("Plannotification", "Wysyłanie powiadomienia!")

                    if (!listitems.isNullOrEmpty()) {

                        var notificationTextklasa = ""
                        var notificationTextzastepca = ""
                        var notificationTextuwagi = ""

                        listitems.forEach { zastdata2 ->
                            notificationTextklasa += zastdata2.klasa + " -- "
                            notificationTextzastepca += zastdata2.zastepca + " -- "
                            notificationTextuwagi += zastdata2.uwagi + " -- "
                        }

                        showNotificationZas(
                            applicationContext,
                            notificationManager,
                            "Następna lekcja (zastępstwo): $notificationTextklasa",
                            "$notificationTextzastepca | $notificationTextuwagi"
                        )
                        Log.d("Plannotification", "Wysyłanie powiadomienia (zastępstwo)!")
                    } else {
                        notificationManager.cancel(2)
                    }
                } else {
                    Log.d("Plannotification", "Dane lekcji się nie zgadzają z porównywanymi danymi")
                }
            }
        }
        return Result.success()
    }

    private fun showNotification(
        applicationContext: Context,
        notificationManager: NotificationManager,
        title: String,
        description: String
    ) {


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
            .setSmallIcon(R.drawable.lacznosc_logo_transparent)
            .setContentIntent(notifyPendingIntent).setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun showNotificationZas(
        applicationContext: Context,
        notificationManager: NotificationManager,
        title: String,
        description: String
    ) {

        // Create an Intent for the activity you want to start.
        val notifyIntent = Intent(applicationContext, MainScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "plan")
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "ZASTEPSTWO")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.lacznosc_logo_transparent)
            .setContentIntent(notifyPendingIntent).setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)

        notificationManager.notify(2, notificationBuilder.build())
    }
}
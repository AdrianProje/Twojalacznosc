package com.ak.twojetlimc.komponenty

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.ak.twojetlimc.MainScreen
import com.ak.twojetlimc.R
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class LiveNotification(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override suspend fun doWork(): Result {

        val currentHour = 0
        val currentLessonTitle = ""
        val currentLessonSubTitle = ""
        val numberOfLessons = 0

        setForegroundAsync(
            ForegroundInfoNotification(
                currentHour,
                currentLessonTitle, currentLessonSubTitle, numberOfLessons
            )
        )

        check()

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private suspend fun check() {
        val accessdata = Datastoremanager(applicationContext)
        if (accessdata.getFavSchedule.first() != "") {

            val htmlvalue = accessdata.getFavSchedule.first()!!.split(",")[1]
            val timestamp = accessdata.getPlanTimestamp.first()

            do {
                val currenthour = getcurrenthour()
                var numberoflessons = 0

                var currentlessontitle = ""
                var currentlessonsubtitle = ""

                accessdata.getSchedule(
                    applicationContext,
                    "$timestamp/$htmlvalue",
                    1
                )!!.plan.forEach {
                    if (it.dzien + 1 == LocalDate.now().dayOfWeek.value) {
                        numberoflessons += 1
                    }
                    if (currenthour == it.numerLekcji && it.dzien + 1 == LocalDate.now().dayOfWeek.value && !it.przedmiot.isEmpty()) {
                        currentlessontitle = "${it.przedmiot}  ${it.czas}"
                        currentlessonsubtitle = "${it.sala}  ${it.nauczyciel} "
                    }
                }



                setForegroundAsync(
                    ForegroundInfoNotification(
                        currenthour,
                        currentlessontitle, currentlessonsubtitle, numberoflessons
                    )
                )

                kotlinx.coroutines.delay(5000)

            } while (currenthour != 0 || currenthour <= numberoflessons)
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun ForegroundInfoNotification(
        progress: Int,
        currentlessontitle: String,
        currentlessonsubtitle: String,
        numberoflessons: Int
    ): ForegroundInfo {

        val listitems = mutableListOf<Notification.ProgressStyle.Segment>()

        val notifyIntent = Intent(applicationContext, MainScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "plan")
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("LiveNotification", "${numberoflessons} ${progress}")
        for (i in 1..numberoflessons) {
            listitems.add(Notification.ProgressStyle.Segment(1).setColor(Color.Yellow.toArgb()))
        }

        var progresstyle =
            Notification.ProgressStyle().setStyledByProgress(true).setProgress(progress)
                .setProgressTrackerIcon(
                    Icon.createWithResource(
                        applicationContext,
                        R.drawable.lacznosc_logo_full_ia
                    )
                )
                .setProgressSegments(
                    listitems
                )

        val notificationBuilder =
            Notification.Builder(applicationContext, "NAZYWO")
                .setContentTitle(currentlessontitle)
                .setContentText(currentlessonsubtitle)
                .setSmallIcon(R.drawable.lacznosc_logo_transparent)
                .setContentIntent(notifyPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setStyle(progresstyle)


        val notification = notificationBuilder.build()

        Log.d("LiveNotification", "Wysyłanie notyfikacji")

        return ForegroundInfo(5, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }
}
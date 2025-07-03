package com.ak.twojetlimc.komponenty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.S)
fun createalarm(context: Context) {
    val times = listOf(
        Pair(7, 10), //alarm 7:10 i tak dalej w dół
        Pair(8, 5),
        Pair(9, 0),
        Pair(9, 55),
        Pair(10, 50),
        Pair(11, 50),
        Pair(12, 45),
        Pair(13, 40),
        Pair(14, 35),
        Pair(15, 30),
        Pair(16, 25),
        Pair(17, 20),
        Pair(18, 15),
        Pair(19, 10)
    )

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    times.forEachIndexed { index, (hour, minute) ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        if (alarmManager.canScheduleExactAlarms()) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index, // Unique request code for each alarm
                Intent(context, BroadcastReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.d("createalarm()", "Ustawiono powiadomienie o godzinie $hour:$minute")
        } else {
            Log.d("createalarm()", "Nie można utworzyć planu brak uprawnień")
        }
    }


    if (alarmManager.canScheduleExactAlarms()) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1000, // Unique request code for each alarm
            Intent(context, SecondBroadcastReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            7200000,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } else {
        Log.d("createalarm()", "Nie można utworzyć planu brak uprawnień")
        Toast.makeText(context, "Brak uprawnień", Toast.LENGTH_SHORT).show()
    }
}

fun switchvibrate(context: Context) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50), -1))
}

fun downlodonlyzas(context: Context): WorkRequest {
    val workRequest2 =
        OneTimeWorkRequestBuilder<ZasCheck>().build()

    WorkManager.getInstance(context)
        .beginWith(workRequest2)
        .enqueue()

    return workRequest2
}

fun downloadplanandzas(context: Context): WorkRequest {

    val workRequest =
        OneTimeWorkRequestBuilder<PlanCheck>().build()
    val workRequest2 =
        OneTimeWorkRequestBuilder<ZasCheck>().build()

    WorkManager.getInstance(context)
        .beginWith(workRequest)
        .then(workRequest2)
        .enqueue()

    return workRequest
}

fun getcurrenthour(): Int {
    val calendarminutes = Calendar.getInstance().get(Calendar.MINUTE)
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        7 -> if (calendarminutes >= 20) 1 else 0

        8 -> if (calendarminutes >= 5) 2 else 1

        9 -> if (calendarminutes >= 55) 4 else 3

        10 -> if (calendarminutes >= 50) 5 else 4

        11 -> if (calendarminutes >= 50) 6 else 5

        12 -> if (calendarminutes >= 45) 7 else 6

        13 -> if (calendarminutes >= 40) 8 else 7

        14 -> if (calendarminutes >= 35) 9 else 8

        15 -> if (calendarminutes >= 30) 10 else 9

        16 -> if (calendarminutes >= 25) 11 else 10

        17 -> if (calendarminutes >= 20) 12 else 11

        18 -> if (calendarminutes >= 15) 13 else 12

        else -> 0
    }
}

//Link na stronę do osadzenia
@Composable
fun WebsiteLink(contextText: String, url: String) {
    val context = LocalContext.current

    Text(
        text = contextText,
        modifier = Modifier.clickable {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        },
        style = TextStyle(
            textDecoration = TextDecoration.Underline,
            color = Color.Blue,
            fontSize = 16.sp
        )
    )
}

//Przyciski widoczne w sekcji info
// Przycisk obraz z linkiem

@Composable
fun ImageLinkButton(modifier: Modifier = Modifier, iconId: Int, link: String) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        },
        modifier = modifier.size(48.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null
        )
    }
}

//Link email otwierający email
@Composable
fun ClickableEmail(email: String) {
    val context = LocalContext.current

    val annotatedString = buildAnnotatedString {
        append("E-mail📨: ")

        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(email)
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable {
                context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                })
            },
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

//Link numer telefonu otwierający telefon
@Composable
fun ClickablePhoneNumber(contextText: String, phoneNumber: String) {
    val context = LocalContext.current

    val annotatedString = buildAnnotatedString {
        append(contextText)
        append(" ")

        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(phoneNumber)
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable {
                context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                })
            },
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun Permissionsinfo() {
    Column {
        Text(
            text = "Zalecane uprawnienia",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Row {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Tworzenie Alarmów i Przypomnień"
            )
            Text(text = "Tworzenie Alarmów i Przypomnień - Służy do aktualizowania danych w aplikacji nawet gdy jest zamknięta lub w tle")
        }


        Text(
            text = "Opcjonalne uprawnienia",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Row {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Powiadomienia")
            Text(text = "Powiadomienia - Służy do wysyłania powiadomień")
        }

        Row {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Instalowanie Aplikacji"
            )
            Text(text = "Instalowanie Aplikacji - Służy do instalowania aktualizacji tej aplikacji")
        }
    }
}
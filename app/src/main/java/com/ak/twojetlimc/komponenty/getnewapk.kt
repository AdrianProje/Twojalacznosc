package com.ak.twojetlimc.komponenty


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@RequiresApi(Build.VERSION_CODES.P)
suspend fun isthereanewversion(context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("isthereanewversion", "Sprawdzanie nowej wersji aplikacji ")
            val url = URL("https://www.tlimc.szczecin.pl/Apka/TwojaLacznosc.apk")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val apkFile = File(context.cacheDir, "TwojaLacznosc.apk")
            connection.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    input.copyTo(output)
                }
            }

            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(
                apkFile.absolutePath,
                PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS
            )

            if (packageInfo!!.longVersionCode > packageManager.getPackageInfo(
                    context.packageName,
                    0
                ).longVersionCode && packageInfo.applicationInfo!!.minSdkVersion <= Build.VERSION.SDK_INT
            ) {
                return@withContext true
            } else {
                File(context.cacheDir, "TwojaLacznosc.apk").delete()
                Log.d("isthereanewversion", "Nie ma nowej wersji aplikacji")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e(
                "isthereanewversion",
                "Wystąpił błąd podczas sprawdzania nowej wersji: ${e.message}"
            )
            return@withContext false
        }
    }
}


fun instalnewversion(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val apkFile = File(context.cacheDir, "TwojaLacznosc.apk")

        if (!apkFile.exists()) {
            Log.d("InstallAPK", "APK file does not exist")
            return@launch
        }

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )

        Log.d("InstallAPK", "APK URI: $apkUri")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("InstallAPK", "Error installing APK: ${e.message}")
            return@launch
        }
    }
}
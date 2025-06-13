package com.ak.twojetlimc.zastepstwa

import android.content.Context
import android.util.Log
import com.ak.twojetlimc.komponenty.Datastoremanager
import it.skrape.core.htmlDocument
import it.skrape.selects.html5.table
import it.skrape.selects.html5.td
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.use
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

fun webscrapeZT(context: Context, htmllink: String, dzien: String) {
    try {
        val url = URL(htmllink)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val htmlfile = File(context.cacheDir, "zast.html")
        connection.inputStream.use { input ->
            FileOutputStream(htmlfile).use { output ->
                input.copyTo(output)
            }
        }

        htmlDocument(htmlfile, Charset.forName("ISO-8859-2")) {
            table {
                val zastDetailsTrs = findAll("tbody") { findAll("tr") }
                Log.d("UnpackZas", zastDetailsTrs.toString())

                zastDetailsTrs.forEach {
                    if ((it.td { findAll { this } }).size == 4) {
                        it.td { findAll { this } }.chunked(4) { chunk ->

                            val numerlekcji = chunk[0].text.toIntOrNull() ?: 0
                            val klasa = chunk[1].text
                            val zastepca = chunk[2].text
                            val uwagi = chunk[3].text

                            val dane = Zastepstwo(
                                numerlekcji,
                                klasa,
                                zastepca,
                                uwagi
                            )

                            val whatclass =
                                klasa.split("-")[0].filter { !it.isWhitespace() }

                            if (numerlekcji != 0) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    Datastoremanager(context).storeZastepstwo(
                                        context,
                                        numerlekcji,
                                        whatclass,
                                        dzien,
                                        1,
                                        dane
                                    )
                                    Log.d("UnpackZas", "Zapisano: $dane na dzień $dzien")
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d("UnpackZas", "Przetworzono dane $dzien")
    } catch (e: Exception) {
        Log.d("UnpackZas", "Błąd $dzien : $e")
    }
}
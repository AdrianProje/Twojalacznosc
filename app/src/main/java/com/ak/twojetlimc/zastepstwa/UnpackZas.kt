package com.ak.twojetlimc.zastepstwa

import android.content.Context
import android.util.Log
import com.ak.twojetlimc.komponenty.Datastoremanager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

fun webscrapeZT(context: Context, htmllink: String, dzien: String) {
    try {

        val htmlfile = Jsoup.connect(htmllink).get()

        val zastTrs = htmlfile.select("tbody").first()!!.getElementsByTag("tr")
        Log.d("UnpackZas", zastTrs.toString())

        zastTrs.forEach { tr ->
            if (tr.getElementsByTag("td").size == 4) {
                tr.getElementsByTag("td").chunked(4) { chunk ->
                    val numerlekcji = chunk[0].text().toIntOrNull() ?: 0
                    val klasa = chunk[1].text() ?: ""
                    val zastepca = chunk[2].text() ?: ""
                    val uwagi = chunk[3].text() ?: ""

                    val dane = Zastepstwo(
                        numerlekcji,
                        klasa,
                        zastepca,
                        uwagi
                    )

                    val whatclass =
                        klasa.split("-")[0].filter { !it.isWhitespace() } ?: klasa

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

        Log.d("UnpackZas", "Przetworzono dane $dzien")
    } catch (e: Exception) {
        Log.d("UnpackZas", "Błąd $dzien : $e")
    }
}
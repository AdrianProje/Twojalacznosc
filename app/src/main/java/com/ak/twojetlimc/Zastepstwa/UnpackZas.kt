package com.ak.twojetlimc.Zastepstwa

import android.content.Context
import android.util.Log
import com.ak.twojetlimc.komponenty.Datastoremanager
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.table
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun webscrapeZT(context: Context, htmllink: String, dzien: String) {
    try {
        skrape(BrowserFetcher) {
            request {
                url = htmllink
                method = Method.GET
            }

            response {
                htmlDocument {
                    table {
                        val zastDetailsTrs = (findAll("tbody")[6].tr { findAll { this } }).drop(3)

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
                                            Log.d("UnpackZas", "Zapisano: $dane")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d("UnpackZas", "Przetworzono dane $dzien")
    } catch (e: Exception) {
        Log.d("UnpackZas", "Błąd $dzien")
    }
}
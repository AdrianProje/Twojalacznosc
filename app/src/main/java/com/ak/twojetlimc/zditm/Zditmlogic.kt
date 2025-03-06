package com.ak.twojetlimc.zditm

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.serialization.json.Json

fun getthedeparturesdata(stopnumber: Int): Tablicaodjazow {
    val jasondata = skrape(BrowserFetcher) {
        request {
            url = "https://www.zditm.szczecin.pl/api/v1/displays/$stopnumber"
            method = Method.GET
//            TODO("Dodać oznaczenie w User-Agent o nazwie aplikacji, Ustalić czy scrapeit ma cachecontrol oraz ETag ")
        }

        response {
            htmlDocument {
                text
            }
        }
    }

    val datatoreturn = Json.decodeFromString<Tablicaodjazow>(jasondata)
    return datatoreturn
}
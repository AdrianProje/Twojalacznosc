package com.ak.twojetlimc.zditm

import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

fun getthedeparturesdata(stopnumber: Int): Tablicaodjazow {
    val jasondata =
        Jsoup.connect("https://www.zditm.szczecin.pl/api/v1/displays/$stopnumber")
            .ignoreContentType(true).execute().body()

    val datatoreturn = Json.decodeFromString<Tablicaodjazow>(jasondata)
    return datatoreturn
}
//            TODO("Dodać oznaczenie w User-Agent o nazwie aplikacji, Ustalić czyJsoup ma cachecontrol oraz ETag ")
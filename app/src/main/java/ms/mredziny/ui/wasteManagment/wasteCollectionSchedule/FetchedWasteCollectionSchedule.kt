package ms.mredziny.ui.wasteManagment.wasteCollectionSchedule

import androidx.room.PrimaryKey
import java.time.Month

data class FetchedWasteCollectionSchedule(
    val dateId: Int,
    val month: String,
    val year: Int,
    val wasteCollectionSchedule: List<Pair<String,List<Int>>>
)

data class FetchedStreet(
    val id: Int = 0,
    val name: String,
)

val monthMap = mapOf(
    "JANUARY" to "styczeń",
    "FEBRUARY" to "luty",
    "MARCH" to "marzec",
    "APRIL" to "kwiecień",
    "MAY" to "maj",
    "JUNE" to "czerwiec",
    "JULY" to "lipiec",
    "AUGUST" to "sierpień",
    "SEPTEMBER" to "wrzesień",
    "OCTOBER" to "październik",
    "NOVEMBER" to "listopad",
    "DECEMBER" to "grudzień"
)


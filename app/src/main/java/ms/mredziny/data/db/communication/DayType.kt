package ms.mredziny.data.db.communication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// dbValue to wartość zapisana w bazie (zgodna z Supabase), label to tekst na przycisku.
@Serializable
enum class DayType(val dbValue: String, val label: String) {
    @SerialName("workday_school") WORKDAY_SCHOOL("workday_school", "Roboczy\nszkolny"),
    @SerialName("workday_summer") WORKDAY_SUMMER("workday_summer", "Roboczy\nwakacyjny"),
    @SerialName("saturday") SATURDAY("saturday", "Soboty"),
    @SerialName("sunday_holiday") SUNDAY_HOLIDAY("sunday_holiday", "Niedziele\ni święta"),
}

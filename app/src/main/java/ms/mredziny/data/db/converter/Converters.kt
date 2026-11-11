package ms.mredziny.data.db.converter

import androidx.room.TypeConverter
import ms.mredziny.data.db.communication.DayType
import ms.mredziny.data.db.wastemanagement.WasteFraction
import java.time.LocalDate
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromDayType(value: DayType?): String? = value?.name?.lowercase()

    @TypeConverter
    fun toDayType(value: String?): DayType? = value?.let {
        runCatching { enumValueOf<DayType>(it.uppercase()) }.getOrNull()
    }

    @TypeConverter
    fun fromWasteFraction(value: WasteFraction?): String? = value?.name?.lowercase()

    @TypeConverter
    fun toWasteFraction(value: String?): WasteFraction? = value?.let {
        runCatching { enumValueOf<WasteFraction>(it.uppercase()) }.getOrNull()
    }
}
package ms.mredziny.data.db.Communication

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScheduleConverter {

    @TypeConverter
    fun fromSchedule(schedule: Map<Int, List<Pair<Int, String>>>): String {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, List<Pair<Int, String>>>>() {}.type
        return gson.toJson(schedule, type)
    }

    @TypeConverter
    fun toSchedule(scheduleString: String): Map<Int, List<Pair<Int, String>>> {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, List<Pair<Int, String>>>>() {}.type
        return gson.fromJson(scheduleString, type)
    }
}
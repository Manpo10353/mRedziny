package ms.mredziny.data.db.communication

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScheduleConverter {

    @TypeConverter
    fun fromSchedule(schedule: Map<Int, List<TimeT>>): String {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, List<TimeT>>>() {}.type
        return gson.toJson(schedule, type)
    }

    @TypeConverter
    fun toSchedule(scheduleString: String): Map<Int, List<TimeT>> {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, List<TimeT>>>() {}.type
        return gson.fromJson(scheduleString, type)
    }
}
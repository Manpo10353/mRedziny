package ms.mredziny.data.db.Communication

import androidx.room.TypeConverter
import com.google.gson.Gson

class PairConverter {

    @TypeConverter
    fun fromString(value: String): Pair<Int, String> {
        val list = Gson().fromJson(value, Array<Any>::class.java).toList()
        return Pair(list[0] as Int, list[1] as String)
    }

    @TypeConverter
    fun fromPair(pair: Pair<Int, String>): String {
        return Gson().toJson(arrayOf(pair.first, pair.second))
    }
}
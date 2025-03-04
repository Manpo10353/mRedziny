package ms.mredziny.data.db.WasteManagment
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.google.gson.*
import java.lang.reflect.Type

class WasteCollectionScheduleConverter {

    private val gson = GsonBuilder().registerTypeAdapter(
        object : TypeToken<List<Pair<String, List<Int>>>>() {}.type,
        PairListAdapter()
    ).create()

    @TypeConverter
    fun fromWasteCollectionSchedule(value: List<Pair<String, List<Int>>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWasteCollectionSchedule(value: String): List<Pair<String, List<Int>>> {
        val type = object : TypeToken<List<Pair<String, List<Int>>>>() {}.type
        return gson.fromJson(value, type)
    }

    // Custom TypeAdapter to handle List<Pair<String, List<Int>>>
    class PairListAdapter : JsonSerializer<List<Pair<String, List<Int>>>>, JsonDeserializer<List<Pair<String, List<Int>>>> {
        override fun serialize(
            src: List<Pair<String, List<Int>>>,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val jsonArray = JsonArray()
            for ((first, second) in src) {
                val jsonPair = JsonArray()
                jsonPair.add(first)
                jsonPair.add(context.serialize(second))
                jsonArray.add(jsonPair)
            }
            return jsonArray
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): List<Pair<String, List<Int>>> {
            val jsonArray = json.asJsonArray
            val list = mutableListOf<Pair<String, List<Int>>>()
            for (element in jsonArray) {
                val pairArray = element.asJsonArray
                val first = pairArray[0].asString
                val second: List<Int> = context.deserialize(pairArray[1], object : TypeToken<List<Int>>() {}.type)
                list.add(first to second)
            }
            return list
        }
    }
}

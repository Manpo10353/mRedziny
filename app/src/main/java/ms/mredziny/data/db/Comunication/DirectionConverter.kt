package ms.mredziny.data.db.Comunication

import androidx.room.TypeConverter

class DirectionConverter {

    private val directionMap = mapOf("Rędziny" to 1, "Częstochowa" to 2)

    @TypeConverter
    fun fromInt(directionInt: Int): String {
        return directionMap.entries.firstOrNull { it.value == directionInt }?.key
            ?: throw IllegalArgumentException("Invalid direction: $directionInt")
    }

    @TypeConverter
    fun toInt(direction: String): Int {
        return directionMap[direction] ?: throw IllegalArgumentException("Invalid direction: $direction")
    }
}
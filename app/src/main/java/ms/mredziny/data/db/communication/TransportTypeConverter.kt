package ms.mredziny.data.db.communication

import androidx.room.TypeConverter

class TransportTypeConverter {

    @TypeConverter
    fun fromTransportTypeList(transportTypes: List<TransportType>?): String? {
        return transportTypes?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toTransportTypeList(data: String?): List<TransportType>? {
        if (data == null) return null
        if (data.isBlank()) return emptyList()
        return data.split(",").mapNotNull { name ->
            runCatching { TransportType.valueOf(name.trim()) }.getOrNull()
        }
    }
}
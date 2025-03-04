package ms.mredziny.data.db.Comunication

import androidx.room.TypeConverter

class TransportTypeConverter {

    @TypeConverter
    fun fromTransportTypeList(transportTypes: List<TransportType>?): String? {
        return transportTypes?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toTransportTypeList(data: String?): List<TransportType>? {
        return data?.split(",")?.map { TransportType.valueOf(it) }
    }
}
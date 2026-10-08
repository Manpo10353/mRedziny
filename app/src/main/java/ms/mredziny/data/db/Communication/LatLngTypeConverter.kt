package ms.mredziny.data.db.Communication

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class LatLngTypeConverter {

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(data: String): LatLng {
        val latLngParts = data.split(",")
        return LatLng(latLngParts[0].toDouble(), latLngParts[1].toDouble())
    }
}
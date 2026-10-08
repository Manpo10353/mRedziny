package ms.mredziny.ui.comunication.busSchedule.busStops

import com.google.android.gms.maps.model.LatLng
import ms.mredziny.data.db.Communication.TransportType

data class FetchedBusStop(
    val id: Int,
    val busStopName: String,
    val directionName: String,
    val cityName: String,
    val favourite: Boolean,
    val transportTypes: List<TransportType>,
    val location: LatLng
)

package ms.mredziny.ui.comunication.busSchedule.busStopSchedule

import com.google.android.gms.maps.model.LatLng
import ms.mredziny.data.db.communication.Schedule

sealed class BusStopScheduleUiState {

    data class Idle(
        val direction: String = "",
        val busStopName: String = "",
        val busStopId: Int = 1,
        val busLine: Int = 1,
        val dayType: Int = 1
    ) : BusStopScheduleUiState()
    data class Loading(
        val direction: String,
        val busStopName: String,
        val busStopId: Int,
        val busLine: Int,
        val dayType: Int
    ) : BusStopScheduleUiState()
    data class Success(
        val busStopSchedule: Schedule,
        val direction: String,
        val busStopName: String,
        val busStopId: Int,
        val busLine: Int,
        val dayType: Int,
        val location: LatLng
    ) : BusStopScheduleUiState()
    data class Error(
        val message: String,
        val direction: String,
        val busStopName: String,
        val busStopId: Int,
        val busLine: Int,
        val dayType: Int
    ) : BusStopScheduleUiState()
}

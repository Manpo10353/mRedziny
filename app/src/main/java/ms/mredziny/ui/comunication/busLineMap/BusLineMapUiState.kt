package ms.mredziny.ui.comunication.busLineMap

import com.google.android.gms.maps.model.LatLng
import ms.mredziny.data.db.Communication.BusStop

sealed class BusLineMapUiState {
    data class Idle(
        val busStopList: List<BusStop> = emptyList(),
        val viewPoint: LatLng = LatLng(50.857524, 19.211380)
    ): BusLineMapUiState()
    data class Loading(
        val busStopList: List<BusStop>,
        val viewPoint: LatLng
    ): BusLineMapUiState()
    data class Success(
        val busStopList: List<BusStop>,
        val viewPoint: LatLng
    ): BusLineMapUiState()
}
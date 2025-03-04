package ms.mredziny.ui.comunication.busSchedule.busStops

sealed class BusStopUiState {
    data class Idle(
        val searchQuery: String = "",
        val direction: String = "",
    ) : BusStopUiState()

    data class Loading(
        val searchQuery: String,
        val direction: String,
        val filteredBusStops: List<FetchedBusStop> = emptyList()
    ) : BusStopUiState()

    data class Success(
        val fetchedBusStops: List<FetchedBusStop>,
        val searchQuery: String,
        val direction: String,
        val filteredBusStops: List<FetchedBusStop> = emptyList()
    ) : BusStopUiState()

    data class Error(
        val message: String,
        val searchQuery: String,
        val direction: String,
        val filteredBusStops: List<FetchedBusStop> = emptyList()
    ) : BusStopUiState()
}
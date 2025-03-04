package ms.mredziny.ui.comunication.busSchedule.busFavourite

import ms.mredziny.ui.comunication.busSchedule.busStops.FetchedBusStop

sealed class BusFavouriteUiState {
    data class Idle(
        var direction: String? = null
    ) : BusFavouriteUiState()

    data class Loading(
        val fetchedBusStops: List<FetchedBusStop>,
        var direction: String? = null
    ) : BusFavouriteUiState()
    data class Success(
        val fetchedBusStops: List<FetchedBusStop>,
        val favouriteBusStops: List<FetchedBusStop>,
        val nearestBusStops: List<Pair<FetchedBusStop, Double>>,
        var direction: String? = null
    ) : BusFavouriteUiState()
    data class Error(
        val message: String,
        var direction: String? = null
    ) : BusFavouriteUiState()
}
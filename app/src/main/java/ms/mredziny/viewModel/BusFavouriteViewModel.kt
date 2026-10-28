package ms.mredziny.viewModel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.mredziny.data.db.communication.BusStopRepository
import ms.mredziny.ui.comunication.busSchedule.busFavourite.BusFavouriteUiState
import ms.mredziny.ui.comunication.busSchedule.busStops.FetchedBusStop
import javax.inject.Inject

@HiltViewModel
class BusFavouriteViewModel @Inject constructor(
    private val repository: BusStopRepository
): ViewModel(){

    private val _uiState = MutableStateFlow<BusFavouriteUiState>(BusFavouriteUiState.Idle())
    val uiState: StateFlow<BusFavouriteUiState> = _uiState.asStateFlow()

    fun setDirection(direction: String) {
        _uiState.value = BusFavouriteUiState.Idle(direction)
        fetchBusStops()
    }

    fun toggleFavourite(busStop: FetchedBusStop) {
        viewModelScope.launch {
            val newFavourite = !busStop.favourite
            repository.updateFavourite(busStop.id, newFavourite)
        }
        val currentState = _uiState.value
        if (currentState !is BusFavouriteUiState.Loading) {
            fetchBusStops()
        }
    }

    private fun fetchBusStops() {
        val direction = when(val currentState = _uiState.value) {
            is BusFavouriteUiState.Idle -> currentState.direction
            is BusFavouriteUiState.Success -> currentState.direction
            is BusFavouriteUiState.Loading -> currentState.direction
            else -> return
        }
        _uiState.value = BusFavouriteUiState.Loading(emptyList())
        viewModelScope.launch {
            try {
                val fetchedBusStops = repository.getFetchedBusStopsByDirection(direction!!)
                _uiState.value = BusFavouriteUiState.Success(
                    fetchedBusStops = fetchedBusStops.map {
                        FetchedBusStop(
                            it.id,
                            it.busStopName,
                            direction,
                            it.cityName,
                            it.favourite,
                            it.transportTypes,
                            it.location
                        )
                    },
                    favouriteBusStops = emptyList(),
                    nearestBusStops = emptyList(),
                    direction = direction
                )
                filterFavouriteBusStops()
                calculateNearestBusStops()
            } catch (e: Exception) {
                _uiState.value = BusFavouriteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    private fun filterFavouriteBusStops() {
        val fetchedBusStop = when(val currentState = _uiState.value) {
            is BusFavouriteUiState.Success -> currentState.fetchedBusStops
            else -> return
        }
        viewModelScope.launch {
            val favouriteBusStops = fetchedBusStop.filter { it.favourite }
            val currentState = _uiState.value
            if (currentState is BusFavouriteUiState.Success) {
                _uiState.value = currentState.copy(favouriteBusStops = favouriteBusStops)
            }
        }
    }

    private fun calculateNearestBusStops() {
        val fetchedBusStop = when(val currentState = _uiState.value) {
            is BusFavouriteUiState.Success -> currentState.fetchedBusStops
            else -> return
        }
        viewModelScope.launch {
            try {
               // val userLocation = locationProvider.getCurrentLocation() // Get user's location
               // val nearestBusStops = busStops
               //     .map { busStop ->
               //         val distance = calculateDistance(userLocation, busStop) // Implement this function
               //         busStop to distance
               //     }
               //     .sortedBy { it.second }
               //     .take(2)
               // // Update state with nearest bus stops, or handle them as needed
            } catch (e: Exception) {
                _uiState.value = BusFavouriteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun calculateDistance(location1: Location, busStop: FetchedBusStop): Double {
        // Implement distance calculation between location1 and busStop
        // For example, using Haversine formula
        return 0.0
    }

}
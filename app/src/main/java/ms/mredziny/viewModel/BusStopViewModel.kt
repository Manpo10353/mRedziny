package ms.mredziny.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.mredziny.data.db.Communication.BusStopRepository
import ms.mredziny.ui.comunication.busSchedule.busStops.BusStopUiState
import ms.mredziny.ui.comunication.busSchedule.busStops.FetchedBusStop
import javax.inject.Inject

@HiltViewModel
class BusStopViewModel @Inject constructor(
    private val repository: BusStopRepository
): ViewModel() {

        private val _uiState = MutableStateFlow<BusStopUiState>(BusStopUiState.Idle())
        val uiState: StateFlow<BusStopUiState> = _uiState.asStateFlow()

    //private val repository = BusStopRepository(busStopDao, directionDao)

    fun updateSearchQuery(newQuery: String) {
        _uiState.value = when (val currentState = _uiState.value) {
            is BusStopUiState.Idle -> currentState.copy(searchQuery = newQuery)
            is BusStopUiState.Loading -> currentState.copy(searchQuery = newQuery)
            is BusStopUiState.Success -> currentState.copy(searchQuery = newQuery)
            is BusStopUiState.Error -> currentState.copy(searchQuery = newQuery)
        }
        val currentState = _uiState.value
        if(currentState is BusStopUiState.Success) {
            filterFetchedBusStops()
        } else if(currentState is BusStopUiState.Idle) {
            fetchBusStops()
        }
    }

    fun setDirection(direction: String) {
        _uiState.value = BusStopUiState.Idle(direction = direction)
        fetchBusStops()
    }
    fun toggleFavourite(busStop: FetchedBusStop) {
        viewModelScope.launch {
            val newFavourite = !busStop.favourite
            repository.updateFavourite(busStop.id, newFavourite)
        }
        val currentState = _uiState.value
        if (currentState !is BusStopUiState.Loading) {
            fetchBusStops()
        }
    }

    private fun fetchBusStops() {
        val (direction, query) = when(val currentState = _uiState.value) {
            is BusStopUiState.Idle -> Pair(currentState.direction, currentState.searchQuery)
            is BusStopUiState.Success -> Pair(currentState.direction, currentState.searchQuery)
            is BusStopUiState.Loading -> Pair(currentState.direction, currentState.searchQuery)
            else -> return
        }
        _uiState.value = BusStopUiState.Loading(query, direction)  // Set to loading state

        viewModelScope.launch {
            try {
                val fetchedBusStops = repository.getFetchedBusStopsByDirection(direction)
                _uiState.value = BusStopUiState.Success(
                    fetchedBusStops.map {
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
                    direction = direction,
                    searchQuery = query,
                )
                filterFetchedBusStops()
            } catch (e: Exception) {
                _uiState.value =
                    BusStopUiState.Error(e.message ?: "Unknown error", query, direction)
            }
        }
    }

    private fun filterFetchedBusStops() {
        val (query, fetchedBusStops) = when(val currentState = _uiState.value) {
            is BusStopUiState.Success -> Pair(currentState.searchQuery, currentState.fetchedBusStops)
            else -> return
        }
        viewModelScope.launch {
            val filteredBusStops = fetchedBusStops.filter { it.busStopName.contains(query, ignoreCase = true) }
            val currentState = _uiState.value
            if (currentState is BusStopUiState.Success) {
                _uiState.value = currentState.copy(filteredBusStops = filteredBusStops)
            }
        }
    }
}
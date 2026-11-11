package ms.mredziny.feature.communication.linemap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ms.mredziny.data.repository.BusStopRepository
import ms.mredziny.model.BusStop
import javax.inject.Inject

@HiltViewModel
class BusLineMapViewModel @Inject constructor(
    private val repository: BusStopRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusLineMapUiState())
    val uiState: StateFlow<BusLineMapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = try {
                BusLineMapUiState(stops = repository.allStops(), isLoading = false)
            } catch (e: Exception) {
                BusLineMapUiState(isLoading = false, error = e.message ?: "Nieznany błąd")
            }
        }
    }

    fun toggleFavourite(stop: BusStop) {
        viewModelScope.launch {
            val isFavourite = !stop.isFavourite
            repository.setFavourite(stop.id, isFavourite)
            _uiState.update { state ->
                state.copy(
                    stops = state.stops.map { current ->
                        if (current.id == stop.id) current.copy(isFavourite = isFavourite) else current
                    },
                )
            }
        }
    }
}

data class BusLineMapUiState(
    val stops: List<BusStop> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

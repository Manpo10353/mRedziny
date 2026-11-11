package ms.mredziny.feature.communication.stops

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ms.mredziny.data.location.LocationSource
import ms.mredziny.data.location.distanceMeters
import ms.mredziny.data.repository.BusStopRepository
import ms.mredziny.data.sync.TimetableSync
import ms.mredziny.model.BusStop
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class BusStopViewModel @Inject constructor(
    private val repository: BusStopRepository,
    private val sync: TimetableSync,
    private val location: LocationSource,
) : ViewModel() {

    private val direction = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow("")
    private var syncJob: Job? = null

    private val _nearest = MutableStateFlow<BusStop?>(null)
    val nearest: StateFlow<BusStop?> = _nearest

    init {
        syncIfNeeded()
    }

    fun syncIfNeeded() {
        if (syncJob?.isActive == true) return
        syncJob = viewModelScope.launch {
            try {
                sync.syncIfNeeded()
            } catch (e: Exception) {
                Log.e("Sync", "Synchronizacja nie powiodła się", e)
            }
        }
    }

    val uiState: StateFlow<BusStopUiState> = combine(direction, query) { dir, q -> dir to q }
        .flatMapLatest { (dir, q) ->
            if (dir == null) {
                flowOf(BusStopUiState(query = q))
            } else {
                repository.stopsByDirection(dir)
                    .map { stops ->
                        BusStopUiState(
                            direction = dir,
                            query = q,
                            stops = stops.filter { it.name.contains(q, ignoreCase = true) },
                            isLoading = false,
                        )
                    }
                    .catch { e ->
                        emit(BusStopUiState(direction = dir, query = q, isLoading = false, error = e.message ?: "Nieznany błąd"))
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BusStopUiState())

    fun setDirection(newDirection: String) {
        direction.value = newDirection
        viewModelScope.launch { findNearest(newDirection) }
    }

    private suspend fun findNearest(dir: String) {
        _nearest.value = null
        val here = try {
            location.current()
        } catch (e: SecurityException) {
            null
        } ?: return

        val stops = repository.stopsByDirection(dir).first()
        _nearest.value = stops.minByOrNull {
            distanceMeters(
                here.latitude, here.longitude,
                it.location.latitude, it.location.longitude,
            )        }
    }

    fun updateQuery(newQuery: String) {
        query.value = newQuery
    }

    fun toggleFavourite(stop: BusStop) {
        viewModelScope.launch { repository.setFavourite(stop.id, !stop.isFavourite) }
    }
}

data class BusStopUiState(
    val direction: String = "",
    val query: String = "",
    val stops: List<BusStop> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

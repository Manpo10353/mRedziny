package ms.mredziny.feature.communication.favourite

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ms.mredziny.data.location.LocationSource
import ms.mredziny.data.repository.BusStopRepository
import ms.mredziny.model.BusStop
import ms.mredziny.model.Coordinates
import java.util.Locale
import javax.inject.Inject

data class NearestStopInfo(
    val stop: BusStop,
    val distanceFormatted: String,
    val distanceMeters: Float,
)

data class BusFavouriteUiState(
    val direction: String = "",
    val favourites: List<BusStop> = emptyList(),
    val nearestStopInfo: NearestStopInfo? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BusFavouriteViewModel @Inject constructor(
    private val repository: BusStopRepository,
    private val locationSource: LocationSource,
) : ViewModel() {

    private val direction = MutableStateFlow<String?>(null)
    private val userLocation = MutableStateFlow<Coordinates?>(null)

    val uiState: StateFlow<BusFavouriteUiState> = combine(
        direction,
        userLocation,
    ) { dir, loc -> dir to loc }
        .flatMapLatest { (dir, loc) ->
            if (dir == null) {
                flowOf(BusFavouriteUiState())
            } else {
                repository.stopsByDirection(dir)
                    .map { stops ->
                        val favourites = stops.filter { it.isFavourite }
                        val nearest = if (loc != null && stops.isNotEmpty()) {
                            stops.minByOrNull { stop -> calculateDistance(loc, stop.location) }?.let { stop ->
                                val meters = calculateDistance(loc, stop.location)
                                NearestStopInfo(
                                    stop = stop,
                                    distanceFormatted = formatDistance(meters),
                                    distanceMeters = meters,
                                )
                            }
                        } else null

                        BusFavouriteUiState(
                            direction = dir,
                            favourites = favourites,
                            nearestStopInfo = nearest,
                            isLoading = false,
                        )
                    }
                    .catch { e ->
                        emit(BusFavouriteUiState(direction = dir, isLoading = false, error = e.message ?: "Nieznany błąd"))
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BusFavouriteUiState())

    fun setDirection(newDirection: String) {
        direction.value = newDirection
    }

    fun updateUserLocation(coordinates: Coordinates) {
        userLocation.value = coordinates
    }

    fun clearUserLocation() {
        userLocation.value = null
    }

    fun locationUpdates() = locationSource.updates().map { location ->
        Coordinates(location.latitude, location.longitude)
    }

    fun toggleFavourite(stop: BusStop) {
        viewModelScope.launch { repository.setFavourite(stop.id, !stop.isFavourite) }
    }

    private fun calculateDistance(from: Coordinates, to: Coordinates): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results,
        )
        return results[0]
    }

    private fun formatDistance(meters: Float): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format(Locale.getDefault(), "%.1f km", meters / 1000f)
        }
    }
}

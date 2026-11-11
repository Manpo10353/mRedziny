package ms.mredziny.feature.communication.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ms.mredziny.data.db.communication.DayType
import ms.mredziny.data.repository.BusScheduleRepository
import ms.mredziny.data.repository.BusStopRepository
import ms.mredziny.model.Coordinates
import ms.mredziny.model.Schedule
import java.time.LocalDate
import javax.inject.Inject

// Wszystko, czego potrzebuje rozkład: przystanek, kierunek, typ dnia i opcjonalnie linia.
data class ScheduleRequest(
    val stopId: Long,
    val stopName: String,
    val direction: String,
    val dayType: DayType,
    val line: String? = null,
)

data class BusStopScheduleUiState(
    val request: ScheduleRequest? = null,
    val schedule: Schedule? = null,
    val location: Coordinates? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class BusStopScheduleViewModel @Inject constructor(
    private val busStopRepository: BusStopRepository,
    private val scheduleRepository: BusScheduleRepository,
) : ViewModel() {

    private val request = MutableStateFlow<ScheduleRequest?>(null)

    val uiState: StateFlow<BusStopScheduleUiState> = request
        .filterNotNull()
        .flatMapLatest { current ->
            flow {
                emit(BusStopScheduleUiState(request = current))
                emit(load(current))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BusStopScheduleUiState())

    // Ustawia zapytanie tylko raz. Typ dnia wynika z dzisiejszej daty.
    fun open(stopId: Long, stopName: String, direction: String) {
        if (request.value != null) return
        viewModelScope.launch {
            val dayType = scheduleRepository.dayTypeFor(LocalDate.now())
            request.value = ScheduleRequest(stopId, stopName, direction, dayType)
        }
    }

    fun selectDayType(dayType: DayType) {
        request.update { it?.copy(dayType = dayType) }
    }

    private suspend fun load(current: ScheduleRequest): BusStopScheduleUiState = try {
        val schedule = scheduleRepository.schedule(current.stopId, current.dayType, current.line)
        val location = busStopRepository.stopById(current.stopId)?.location
        BusStopScheduleUiState(
            request = current,
            schedule = schedule,
            location = location,
            isLoading = false,
            error = if (schedule.times.isEmpty()) "Brak odjazdów dla wybranych opcji" else null,
        )
    } catch (e: Exception) {
        BusStopScheduleUiState(request = current, isLoading = false, error = e.message ?: "Nieznany błąd")
    }
}

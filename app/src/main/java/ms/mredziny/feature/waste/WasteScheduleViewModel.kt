package ms.mredziny.feature.waste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ms.mredziny.data.repository.WasteScheduleRepository
import ms.mredziny.model.Place
import ms.mredziny.model.Street
import ms.mredziny.model.WasteSchedule
import java.time.YearMonth
import javax.inject.Inject

enum class Presentation { LIST, CALENDAR }

data class WasteScheduleUiState(
    val places: List<Place> = emptyList(),
    val streets: List<Street> = emptyList(),
    val months: List<YearMonth> = emptyList(),
    val selectedPlace: Place? = null,
    val selectedStreet: Street? = null,
    val selectedMonth: YearMonth = YearMonth.now(),
    val schedule: WasteSchedule? = null,
    val presentation: Presentation = Presentation.LIST,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class WasteScheduleViewModel @Inject constructor(
    private val repository: WasteScheduleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WasteScheduleUiState())
    val uiState: StateFlow<WasteScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val places = repository.places()
                val months = repository.availableMonths()
                // Domyślnie bieżący miesiąc, a jeśli go nie ma, najbliższy dostępny.
                val now = YearMonth.now()
                val selected = if (now in months) now else months.firstOrNull { it >= now } ?: months.lastOrNull() ?: now
                _uiState.update { it.copy(places = places, months = months, selectedMonth = selected) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Nieznany błąd") }
            }
        }
    }

    fun selectPlace(place: Place) {
        _uiState.update {
            it.copy(selectedPlace = place, selectedStreet = null, streets = emptyList(), schedule = null)
        }
        viewModelScope.launch {
            try {
                val streets = repository.streets(place.id)
                _uiState.update { it.copy(streets = streets) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Nieznany błąd") }
            }
        }
    }

    fun selectStreet(street: Street) {
        _uiState.update { it.copy(selectedStreet = street) }
        loadSchedule()
    }

    fun selectMonth(month: YearMonth) {
        _uiState.update { it.copy(selectedMonth = month) }
        loadSchedule()
    }

    fun selectPresentation(presentation: Presentation) {
        _uiState.update { it.copy(presentation = presentation) }
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            val current = _uiState.value
            val street = current.selectedStreet ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val schedule = repository.schedule(street.id, current.selectedMonth)
                _uiState.update { it.copy(schedule = schedule, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Nieznany błąd") }
            }
        }
    }
}

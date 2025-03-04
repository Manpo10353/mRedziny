package ms.mredziny.ui.wasteManagment.wasteCollectionSchedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.mredziny.data.db.WasteManagment.Date
import ms.mredziny.data.db.WasteManagment.Place
import ms.mredziny.data.db.WasteManagment.WasteCollectionScheduleRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WasteCollectionScheduleViewModel @Inject constructor(
    private val repository: WasteCollectionScheduleRepository
) : ViewModel(
) {

    private val current: LocalDate = LocalDate.now()
    private val month = current.month.toString()
    private val year = current.year

    private val _uiState = MutableStateFlow<WasteCollectionScheduleUiState>(WasteCollectionScheduleUiState.Idle())
    val uiState: StateFlow<WasteCollectionScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val places = setPlacesNames()
            val dates = setDates()
            val dateId = dates.find { it.month == month && it.year == year }?.id ?: 0
            val currentState = _uiState.value
            if (currentState is WasteCollectionScheduleUiState.Idle) {
                val updatedState = currentState.copy(
                    placesList = places,
                    dateList = dates,
                    date = Date(id = dateId, month = month, year = year)

                )
                if (updatedState != currentState) {
                    _uiState.value = updatedState
                }
            }
        }
    }
    private suspend fun setPlacesNames(): List<Place> {
        return repository.getAllPlaces()
    }

    private suspend fun setDates(): List<Date> {
        return repository.getAllDate()
    }

    fun setPresentationType(presentationType: Int) {
        _uiState.value = when (val currentState = _uiState.value) {
            is WasteCollectionScheduleUiState.Success -> currentState.copy(presentationType = presentationType)
            else -> return
        }
    }

    private fun setStreetsNames() {
        viewModelScope.launch {
            _uiState.value = when (val currentState = _uiState.value) {
                is WasteCollectionScheduleUiState.PickedPlace -> {
                    val streetsList = repository.getStreetByPlace(currentState.pickedPlace)
                    currentState.copy(streetsList = streetsList)
                }
            else -> return@launch
        }
        }

    }

    fun setPlaceName(it: Place) {
        _uiState.value = when (val currentState = _uiState.value) {
            is WasteCollectionScheduleUiState.Idle -> WasteCollectionScheduleUiState.PickedPlace(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                it,
                FetchedStreet(id=0,name = ""),
                currentState.date
            )

            is WasteCollectionScheduleUiState.PickedPlace -> currentState.copy(pickedPlace = it)
            is WasteCollectionScheduleUiState.PickedStreet -> WasteCollectionScheduleUiState.PickedPlace(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                it,
                FetchedStreet(id=0,name = ""),
                currentState.date
            )

            is WasteCollectionScheduleUiState.PickedDate -> WasteCollectionScheduleUiState.PickedPlace(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                it,
                FetchedStreet(id=0,name = ""),
                currentState.date
            )

            is WasteCollectionScheduleUiState.Success -> WasteCollectionScheduleUiState.PickedPlace(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                it,
                FetchedStreet(id=0,name = ""),
                currentState.date
            )

            else -> return
        }
        setStreetsNames()
    }

    fun setStreetName(it: FetchedStreet) {
        _uiState.value = when (val currentState = _uiState.value) {
            is WasteCollectionScheduleUiState.PickedPlace -> WasteCollectionScheduleUiState.PickedStreet(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                currentState.pickedPlace,
                it,
                currentState.date
            )

            is WasteCollectionScheduleUiState.PickedStreet -> currentState.copy(pickedStreet = it)
            is WasteCollectionScheduleUiState.PickedDate -> WasteCollectionScheduleUiState.PickedStreet(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                currentState.pickedPlace,
                it,
                currentState.date
            )

            is WasteCollectionScheduleUiState.Success -> WasteCollectionScheduleUiState.PickedStreet(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                currentState.pickedPlace,
                it,
                currentState.date
            )

            else -> return
        }
        getSchedule()
    }

    fun setDate(it: Date) {
        _uiState.value = when (val currentState = _uiState.value) {
            is WasteCollectionScheduleUiState.PickedStreet -> WasteCollectionScheduleUiState.PickedDate(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                currentState.pickedPlace,
                currentState.pickedStreet,
                Date(
                    id = it.id,
                    month = it.month,
                    year = it.year
                ),
                0

            )
            is WasteCollectionScheduleUiState.Success -> WasteCollectionScheduleUiState.PickedDate(
                currentState.placesList,
                currentState.streetsList,
                currentState.dateList,
                currentState.pickedPlace,
                currentState.pickedStreet,
                Date(
                    id = it.id,
                    month = it.month,
                    year = it.year
                ),
                currentState.presentationType
            )

            is WasteCollectionScheduleUiState.PickedDate -> currentState.copy(

            )

            else -> return
        }
        getSchedule()
    }

    private fun getSchedule() {
        viewModelScope.launch {
        when (val currentState = _uiState.value) {
            is WasteCollectionScheduleUiState.PickedDate -> {
                try{
                val fetchedSchedule = repository.getWasteCollectionScheduleByStreetAndDate(
                    currentState.pickedStreet.id,
                    currentState.date

                )
                _uiState.value = WasteCollectionScheduleUiState.Success(
                    currentState.placesList,
                    currentState.streetsList,
                    currentState.dateList,
                    currentState.pickedPlace,
                    currentState.pickedStreet,
                    currentState.date,
                    fetchedSchedule,
                    currentState.presentationType
                )}catch (e: Exception){
                    _uiState.value = WasteCollectionScheduleUiState.Error(
                        "Brak danych",
                        currentState.placesList,
                        currentState.streetsList,
                        currentState.dateList,
                        currentState.pickedPlace,
                        currentState.pickedStreet,
                        currentState.date,
                    )
                }
            }
            is WasteCollectionScheduleUiState.PickedStreet -> {
                try{
                val fetchedSchedule = repository.getWasteCollectionScheduleByStreetAndDate(
                    currentState.pickedStreet.id,
                    Date(
                        id = currentState.date.id,
                        month = currentState.date.month,
                        year = currentState.date.year

                    )
                )
                _uiState.value = WasteCollectionScheduleUiState.Success(
                    currentState.placesList,
                    currentState.streetsList,
                    currentState.dateList,
                    currentState.pickedPlace,
                    currentState.pickedStreet,
                    Date(
                        id = currentState.date.id,
                        month = currentState.date.month,
                        year = currentState.date.year

                    ),
                    fetchedSchedule,
                    0
                )}catch (e: Exception){
                    _uiState.value = WasteCollectionScheduleUiState.Error(
                        "$e",
                        currentState.placesList,
                        currentState.streetsList,
                        currentState.dateList,
                        currentState.pickedPlace,
                        currentState.pickedStreet,
                        Date(
                            id = currentState.date.id,
                            month = currentState.date.month,
                            year = currentState.date.year

                        ),
                    )
                }
            }
            else -> _uiState.value = WasteCollectionScheduleUiState.Error(
                "Brak danych",
                emptyList(),
                emptyList(),
                emptyList(),
                Place(id = 0, name = ""),
                FetchedStreet(id = 0, name = ""),
                Date(
                    id = 0,
                    month = "",
                    year = 0

                )
            )
        }
    }
        }
    }


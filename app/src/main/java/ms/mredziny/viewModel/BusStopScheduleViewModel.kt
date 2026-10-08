package ms.mredziny.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.mredziny.data.db.Communication.BusStopRepository
import ms.mredziny.ui.comunication.busSchedule.busStopSchedule.BusStopScheduleUiState
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BusStopScheduleViewModel @Inject constructor(
    private val repository: BusStopRepository
):ViewModel() {
    private val _uiState = MutableStateFlow<BusStopScheduleUiState>(BusStopScheduleUiState.Idle())
    val uiState: StateFlow<BusStopScheduleUiState> = _uiState.asStateFlow()

    private fun checkDayType(): Int {
        val currentDate = LocalDate.now()
        val currentDayOfWeek = currentDate.dayOfWeek
        val currentMonthValue = currentDate.month.value

        return when (currentDayOfWeek) {
            DayOfWeek.SATURDAY -> 2// "Sobota"
            DayOfWeek.SUNDAY -> 3//"Niedziele i święta" //dodać rozróżnianie świąt jakoś z listy
            else -> {
                when (currentMonthValue) {
                    in 7..8 -> 4//"Roboczy wakacyjny"
                    else -> 1 //"Roboczy szkolny"
                }
            }
        }
    }

    fun setBusLine(newBusLine: Int) {
        val direction: String
        val busStopName: String
        val busStopId: Int
        val dayType: Int

        when (val currentState = _uiState.value) {
            is BusStopScheduleUiState.Success -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
            }
            is BusStopScheduleUiState.Error -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
            }
            is BusStopScheduleUiState.Loading -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
            }
            is BusStopScheduleUiState.Idle -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
            }
        }
        _uiState.value = BusStopScheduleUiState.Loading(
            direction = direction,
            busStopName = busStopName,
            busStopId = busStopId,
            dayType = dayType,
            busLine = newBusLine
        )
        Log.d("BusStopScheduleViewModel", "setBusLine: $newBusLine")
        fetchBusStopSchedule()
    }

    fun setDayType(newDayType: Int) {
        val direction: String
        val busStopName: String
        val busStopId: Int
        val busLine: Int

        when (val currentState = _uiState.value) {
            is BusStopScheduleUiState.Success -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Error -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Loading -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Idle -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                busLine = currentState.busLine
            }
        }
        _uiState.value = BusStopScheduleUiState.Loading(
            direction = direction,
            busStopName = busStopName,
            busStopId = busStopId,
            dayType = newDayType,
            busLine = busLine
        )
        fetchBusStopSchedule()
    }

    fun setDirectionAndBusStopName(busStopId: Int,busStopName: String,direction: String ){
        val dayType = checkDayType()
        val currentState = _uiState.value
        when(currentState){
            is BusStopScheduleUiState.Idle -> _uiState.value = BusStopScheduleUiState.Loading(
                busStopId = busStopId,
                busStopName = busStopName,
                direction = direction,
                dayType = dayType,
                busLine = 1
            )
            else -> return
        }
        fetchBusStopSchedule()
    }

    private fun fetchBusStopSchedule(){
        val direction: String
        val busStopName: String
        val busStopId: Int
        val dayType: Int
        val busLine: Int

        when (val currentState = _uiState.value) {
            is BusStopScheduleUiState.Success -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Error -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Loading -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
                busLine = currentState.busLine
            }
            is BusStopScheduleUiState.Idle -> {
                direction = currentState.direction
                busStopName = currentState.busStopName
                busStopId = currentState.busStopId
                dayType = currentState.dayType
                busLine = currentState.busLine
            }
        }
            _uiState.value = BusStopScheduleUiState.Loading(
                direction = direction,
                busStopId = busStopId,
                busStopName = busStopName,
                dayType = dayType,
                busLine = busLine
            )
            viewModelScope.launch {
                try {
                    val busStopSchedule = repository.getBusStopSchedule(busStopId,dayType,busLine)
                    val busSchedule = repository.getScheduleById(busStopSchedule)
                    val location = repository.getBusStopLocation(busStopId)
                    _uiState.value = BusStopScheduleUiState.Success(
                        busSchedule,
                        direction = direction,
                        busStopId = busStopId,
                        busStopName = busStopName,
                        dayType = dayType,
                        busLine = busLine,
                        location = location
                    )
                } catch (e: Exception) {
                    _uiState.value = BusStopScheduleUiState.Error(
                        e.message ?: "Unknown error",
                        direction = direction,
                        busStopId = busStopId,
                        busStopName = busStopName,
                        dayType = dayType,
                        busLine = busLine
                    )
                }
        }
    }
}
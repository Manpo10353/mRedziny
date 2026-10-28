package ms.mredziny.ui.wasteManagment.wasteCollectionSchedule

import ms.mredziny.data.db.wastemanagement.Date
import ms.mredziny.data.db.wastemanagement.Place

sealed class WasteCollectionScheduleUiState{
    data class Idle(
        val placesList: List<Place> = emptyList(),
        val streetsList: List<FetchedStreet> = emptyList(),
        val dateList: List<Date> = emptyList(),
        val pickedPlace: Place = Place(id =0,name = ""),
        val pickedStreet: FetchedStreet = FetchedStreet(id =0,name = ""),
        val date: Date = Date(id = 0, month = "", year = 0),
    ) : WasteCollectionScheduleUiState()

    data class PickedPlace(
        val placesList: List<Place>,
        val streetsList: List<FetchedStreet>,
        val dateList: List<Date>,
        val pickedPlace: Place,
        val pickedStreet: FetchedStreet,
        val date: Date,
    ) : WasteCollectionScheduleUiState()

    data class PickedStreet(
        val placesList: List<Place>,
        val streetsList: List<FetchedStreet>,
        val dateList: List<Date>,
        val pickedPlace: Place,
        val pickedStreet: FetchedStreet,
        val date: Date,
    ) : WasteCollectionScheduleUiState()

    data class PickedDate(
        val placesList: List<Place>,
        val streetsList: List<FetchedStreet>,
        val dateList: List<Date>,
        val pickedPlace: Place,
        val pickedStreet: FetchedStreet,
        val date: Date,
        val presentationType: Int
    ) : WasteCollectionScheduleUiState()

    data class Success(
        val placesList: List<Place>,
        val streetsList: List<FetchedStreet>,
        val dateList: List<Date>,
        val pickedPlace: Place,
        val pickedStreet: FetchedStreet,
        val date: Date,
        val wasteCollectionSchedule: FetchedWasteCollectionSchedule? = null,
        val presentationType: Int
    ) : WasteCollectionScheduleUiState()

    data class Error(
        val message: String,
        val placesList: List<Place>,
        val streetsList: List<FetchedStreet>,
        val dateList: List<Date>,
        val pickedPlace: Place,
        val pickedStreet: FetchedStreet,
        val date: Date,
    ) : WasteCollectionScheduleUiState()
}

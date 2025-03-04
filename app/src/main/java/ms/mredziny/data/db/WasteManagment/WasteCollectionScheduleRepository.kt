package ms.mredziny.data.db.WasteManagment

import android.util.Log
import ms.mredziny.data.db.WasteCollectionScheduleDao
import ms.mredziny.ui.wasteManagment.wasteCollectionSchedule.FetchedStreet
import ms.mredziny.ui.wasteManagment.wasteCollectionSchedule.FetchedWasteCollectionSchedule
import javax.inject.Inject

class WasteCollectionScheduleRepository@Inject constructor(
    private val wasteCollectionScheduleDao: WasteCollectionScheduleDao
)
{
    suspend fun insert(WasteCollectionSchedule: WasteCollectionSchedule) {
        wasteCollectionScheduleDao.insert(WasteCollectionSchedule)
    }

    suspend fun getAll(): List<WasteCollectionSchedule> {
        return wasteCollectionScheduleDao.getAll()
    }

    suspend fun deleteAll() {
        wasteCollectionScheduleDao.deleteAll()
    }

    suspend fun getStreetByPlace(place: Place): List<FetchedStreet> {
        val streets = wasteCollectionScheduleDao.getStreetByPlace(place.id).sortedBy { it.name }
        return streets.map { FetchedStreet(it.id, it.name) }

    }

    suspend fun getAllPlaces(): List<Place> {
        return wasteCollectionScheduleDao.getAllPlaces().sortedBy { it.name }
    }

    suspend fun getWasteScheduleById(id: Int): WasteSchedule {
        return wasteCollectionScheduleDao.getWasteScheduleById(id)
    }

    suspend fun getAllDate(): List<Date> {
        return wasteCollectionScheduleDao.getAllDate()
    }

    suspend fun getWasteCollectionScheduleByStreetAndDate(streetId: Int,date: Date): FetchedWasteCollectionSchedule {
        val wasteCollectionSchedule = wasteCollectionScheduleDao.getWasteCollectionSchedule(streetId, date.id)
        return if(wasteCollectionSchedule == null) {
            FetchedWasteCollectionSchedule(dateId = date.id,date.month, date.year, wasteCollectionSchedule = listOf(Pair("0", emptyList())))
        } else{
            val wasteSchedule = wasteCollectionScheduleDao.getWasteScheduleById(wasteCollectionSchedule.scheduleId)
            FetchedWasteCollectionSchedule(
                dateId = wasteCollectionSchedule.dateId,
                month = date.month,
                year = date.year,
                wasteCollectionSchedule = wasteSchedule.schedule)

        }
        }
}
package ms.mredziny.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.gms.maps.model.LatLng
import ms.mredziny.data.db.Comunication.BusLine
import ms.mredziny.data.db.Comunication.BusStop
import ms.mredziny.data.db.Comunication.BusStopSchedule
import ms.mredziny.data.db.Comunication.City
import ms.mredziny.data.db.Comunication.DayType
import ms.mredziny.data.db.Comunication.Direction
import ms.mredziny.data.db.Comunication.Schedule
import ms.mredziny.data.db.WasteManagment.Date
import ms.mredziny.data.db.WasteManagment.Place
import ms.mredziny.data.db.WasteManagment.Street
import ms.mredziny.data.db.WasteManagment.WasteCollectionSchedule
import ms.mredziny.data.db.WasteManagment.WasteSchedule
import ms.mredziny.ui.wasteManagment.wasteCollectionSchedule.FetchedWasteCollectionSchedule

@Dao
interface BusStopDao {

    @Query("SELECT * FROM busstop")
    suspend fun getAllBusStops(): List<BusStop>

    @Query("SELECT * FROM busstop WHERE busStopName LIKE :name AND directionId = :direction")
    suspend fun getBusStopByNameAndDirection(name: String, direction: String): List<BusStop>
    //i wont be using this function, because i will search busStops by name in domain layer

    @Query("SELECT * FROM busstop WHERE directionId = :directionId")
    suspend fun getBusStopByDirection(directionId: Int): List<BusStop>

    @Query("SELECT * FROM busstop WHERE directionId = :directionId AND favourite = 1")
    suspend fun getFavouriteBusStopByDirection(directionId: Int): List<BusStop>

    @Query("UPDATE busstop SET favourite = :favourite WHERE id = :busStopId")
    suspend fun updateFavourite(busStopId: Int, favourite: Boolean)

    @Query("SELECT location FROM busstop WHERE id = :busStopId")
    suspend fun getBusStopLocation(busStopId: Int): LatLng
}

@Dao
interface DirectionDao {

    @Query("SELECT * FROM Direction")
    suspend fun getAllDirections(): List<Direction>

    @Query("SELECT id FROM direction WHERE directionName = :directionName LIMIT 1")
    suspend fun getDirectionIdByName(directionName: String): Int?

    @Query("SELECT * FROM direction WHERE id = :directionId LIMIT 1")
    suspend fun getDirectionById(directionId: Int): Direction?
}

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    suspend fun getAllCities(): List<City>

    @Query("SELECT * FROM city WHERE id = :cityId LIMIT 1")
    suspend fun getCityById(cityId: Int): City?
}

@Dao
interface BusLineDao {

    @Query("SELECT * FROM busline")
    suspend fun getAllBusLines(): List<BusLine>

    @Query("SELECT * FROM busline WHERE id = :id")
    suspend fun getBusLinesId(id: Int): List<BusLine>
}

@Dao
interface BusStopScheduleDao {

    @Query("SELECT * FROM busStopSchedules WHERE busStopId = :busStopId AND dayTypeId = :dayTypeId")
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int): List<BusStopSchedule>

    @Query("SELECT scheduleId FROM busStopSchedules WHERE busStopId = :busStopId AND dayTypeId = :dayTypeId AND busLineId = :busLineId")
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int, busLineId: Int): Int

    @Query("SELECT * FROM busStopSchedules WHERE busStopId = :busStopId AND dayTypeId = :dayTypeId AND busLineId = :busLineId AND scheduleId = :scheduleId")
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int, busLineId: Int, scheduleId: Int): List<BusStopSchedule>
}

@Dao
interface DayTypeDao {

    @Query("SELECT * FROM daytype")
    suspend fun getAllDayTypes(): List<DayType>

    @Query("SELECT * FROM daytype WHERE id = :id")
    suspend fun getDayTypeById(id: Int): DayType?
}

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule")
    suspend fun getAllSchedules(): List<Schedule>

    @Query("SELECT * FROM schedule WHERE id IN (:id)")
    suspend fun getScheduleById(id: Int): Schedule

}

@Dao
interface WasteCollectionScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fetchedWasteCollectionSchedule: WasteCollectionSchedule)

    @Query("SELECT * FROM WasteCollectionSchedule")
    suspend fun getAll(): List<WasteCollectionSchedule>

    @Query("DELETE FROM WasteCollectionSchedule")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    @Query("SELECT * FROM Place")
    suspend fun getAllPlaces(): List<Place>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreet(street: Street)

    @Query("SELECT * FROM Street")
    suspend fun getAllStreets(): List<Street>

    @Query("SELECT * FROM Street WHERE placeId = :placeId")
    suspend fun getStreetByPlace(placeId: Int): List<Street>

    @Query("SELECT * FROM WasteCollectionSchedule WHERE streetId = :streetId AND dateId = :dateId LIMIT 1")
    suspend fun getWasteCollectionSchedule(streetId: Int, dateId: Int): WasteCollectionSchedule?

    @Query("SELECT * FROM WasteSchedule WHERE id = :scheduleId")
    suspend fun getWasteScheduleById(scheduleId: Int): WasteSchedule

    @Query("SELECT * FROM Date")
    suspend fun getAllDate(): List<Date>

    @Query("SELECT * FROM Date WHERE id = :dateId")
    suspend fun getDateById(dateId: Int): Date
}

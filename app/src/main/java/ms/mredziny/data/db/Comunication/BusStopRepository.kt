package ms.mredziny.data.db.Comunication

import com.google.android.gms.maps.model.LatLng
import ms.mredziny.data.db.BusLineDao
import ms.mredziny.data.db.BusStopDao
import ms.mredziny.data.db.BusStopScheduleDao
import ms.mredziny.data.db.CityDao
import ms.mredziny.data.db.DayTypeDao
import ms.mredziny.data.db.DirectionDao
import ms.mredziny.data.db.ScheduleDao
import ms.mredziny.ui.comunication.busSchedule.busStops.FetchedBusStop
import javax.inject.Inject

class BusStopRepository @Inject constructor(
    private val busStopDao: BusStopDao,
    private val directionDao: DirectionDao,
    private val cityDao: CityDao,
    private val busLineDao: BusLineDao,
    private val busStopScheduleDao: BusStopScheduleDao,
    private val dayTypeDao: DayTypeDao,
    private val scheduleDao: ScheduleDao
){

    suspend fun getAllBusStops(): List<BusStop> {
            return busStopDao.getAllBusStops()
        }

    suspend fun getBusStopByNameAndDirection(name: String, direction: String): List<BusStop> {
            return busStopDao.getBusStopByNameAndDirection(name, direction)
        }

    suspend fun updateFavourite(busStopId: Int, favourite: Boolean) {
        busStopDao.updateFavourite(busStopId, favourite)
    }

    suspend fun getFavoriteBusStopByDirection(directionName: String): List<FetchedBusStop> {
        val directionId = directionDao.getDirectionIdByName(directionName) ?: return emptyList()
        val busStops = busStopDao.getFavouriteBusStopByDirection(directionId)

        // Map BusStop to FetchedBusStop with additional information
        return busStops.map { busStop ->
            val direction = directionDao.getDirectionById(busStop.directionId)?.directionName ?: ""
            val city = cityDao.getCityById(busStop.cityId)?.cityName ?: ""
            FetchedBusStop(
                busStop.id,
                busStop.busStopName,
                direction,
                city,
                busStop.favourite,
                busStop.transportTypes,
                busStop.location
            )
        }
    }

    suspend fun getFetchedBusStopsByDirection(directionName: String): List<FetchedBusStop> {
        val directionId = directionDao.getDirectionIdByName(directionName) ?: return emptyList()
        val busStops = busStopDao.getBusStopByDirection(directionId)

        // Map BusStop to FetchedBusStop with additional information
        return busStops.map { busStop ->
            val direction = directionDao.getDirectionById(busStop.directionId)?.directionName ?: ""
            val city = cityDao.getCityById(busStop.cityId)?.cityName ?: ""
            FetchedBusStop(
                busStop.id,
                busStop.busStopName,
                direction,
                city,
                busStop.favourite,
                busStop.transportTypes,
                busStop.location
            )
        }
    }

    suspend fun getAllDirections(): List<Direction> {
            return directionDao.getAllDirections()
        }

    suspend fun getAllCities(): List<City> {
            return cityDao.getAllCities()
        }

    suspend fun getAllBusLines(): List<BusLine> {
            return busLineDao.getAllBusLines()
        }
    suspend fun getBusLinesById(id: Int): List<BusLine> {
            return busLineDao.getBusLinesId(id)
        }
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int): List<BusStopSchedule> {
            return busStopScheduleDao.getBusStopSchedule(busStopId, dayTypeId)
        }
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int, busLineId: Int): Int {
            return busStopScheduleDao.getBusStopSchedule(busStopId, dayTypeId, busLineId)
        }
    suspend fun getBusStopSchedule(busStopId: Int, dayTypeId: Int, busLineId: Int, scheduleId: Int): List<BusStopSchedule> {
            return busStopScheduleDao.getBusStopSchedule(busStopId, dayTypeId, busLineId, scheduleId)
        }//wont be using this function
    suspend fun getDirectionIdByName(directionName: String): Int? {
            return directionDao.getDirectionIdByName(directionName)
        }
    suspend fun getDirectionById(directionId: Int): Direction? {
            return directionDao.getDirectionById(directionId)
        }
    suspend fun getCityById(cityId: Int): City? {
            return cityDao.getCityById(cityId)
        }
    suspend fun getDayTypeById(dayTypeId: Int): DayType? {
            return dayTypeDao.getDayTypeById(dayTypeId)
        }
    suspend fun getScheduleById(scheduleId: Int): Schedule {
            return scheduleDao.getScheduleById(scheduleId)
        }
    suspend fun getAllDayTypes(): List<DayType> {
            return dayTypeDao.getAllDayTypes()
        }
    suspend fun getAllSchedules(): List<Schedule> {
            return scheduleDao.getAllSchedules()
        }

    suspend fun getBusStopLocation(busStopId: Int): LatLng {
        return busStopDao.getBusStopLocation(busStopId)

    }


}
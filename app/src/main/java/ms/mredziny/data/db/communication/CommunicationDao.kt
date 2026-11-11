package ms.mredziny.data.db.communication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CommunicationDao {

    @Query(
        """
        SELECT b.* FROM bus_stop b
        JOIN direction d ON d.id = b.direction_id
        WHERE d.name = :direction
        ORDER BY b.name
        """
    )
    abstract fun stopsByDirection(direction: String): Flow<List<BusStopEntity>>

    @Query("SELECT * FROM bus_stop ORDER BY name")
    abstract suspend fun allStops(): List<BusStopEntity>

    @Query("SELECT * FROM bus_stop WHERE id = :stopId")
    abstract suspend fun stopById(stopId: Long): BusStopEntity?

    @Query("SELECT DISTINCT d.busStopId AS stopId, l.operator AS operator FROM departure d JOIN bus_line l ON l.id = d.busLineId")
    abstract suspend fun stopOperators(): List<StopOperatorRow>

    @Query(
        """
        SELECT d.departureTime AS time, l.name AS line, l.operator AS operator, l.description AS description
        FROM departure d
        JOIN bus_line l ON l.id = d.busLineId
        WHERE d.busStopId = :stopId AND d.dayType = :dayType
        ORDER BY d.departureTime
        """
    )
    abstract suspend fun departuresForStop(stopId: Long, dayType: String): List<DepartureRow>

    @Query("SELECT EXISTS(SELECT 1 FROM public_holiday WHERE holidayDate = :date)")
    abstract suspend fun isHoliday(date: String): Boolean

    @Query("SELECT * FROM favourite_stop")
    abstract fun favourites(): Flow<List<FavouriteStopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addFavourite(item: FavouriteStopEntity)

    @Query("DELETE FROM favourite_stop WHERE stopId = :stopId")
    abstract suspend fun removeFavourite(stopId: Long)

    // directions ma domyślną wartość, więc dotychczasowe wywołania nadal się kompilują.
    @Transaction
    open suspend fun replaceAll(
        stops: List<BusStopEntity>,
        lines: List<BusLineEntity>,
        departures: List<DepartureEntity>,
        holidays: List<PublicHolidayEntity>,
        directions: List<DirectionEntity> = emptyList(),
    ) {
        clearDepartures()
        clearStops()
        clearLines()
        clearHolidays()
        if (directions.isNotEmpty()) {
            clearDirections()
            insertDirections(directions)
        }
        insertStops(stops)
        insertLines(lines)
        insertDepartures(departures)
        insertHolidays(holidays)
    }

    @Query("DELETE FROM departure") abstract suspend fun clearDepartures()
    @Query("DELETE FROM bus_stop") abstract suspend fun clearStops()
    @Query("DELETE FROM bus_line") abstract suspend fun clearLines()
    @Query("DELETE FROM public_holiday") abstract suspend fun clearHolidays()
    @Query("DELETE FROM direction") abstract suspend fun clearDirections()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertStops(items: List<BusStopEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLines(items: List<BusLineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDepartures(items: List<DepartureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHolidays(items: List<PublicHolidayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDirections(items: List<DirectionEntity>)
}

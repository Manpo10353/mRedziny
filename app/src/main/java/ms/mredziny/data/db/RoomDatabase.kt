package ms.mredziny.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ms.mredziny.data.db.wastemanagement.Place
import ms.mredziny.data.db.wastemanagement.Street
import ms.mredziny.data.db.wastemanagement.WasteCollectionSchedule
import ms.mredziny.data.db.wastemanagement.WasteCollectionScheduleConverter
import ms.mredziny.data.db.communication.Direction
import ms.mredziny.data.db.communication.BusStop
import ms.mredziny.data.db.communication.BusStopSchedule
import ms.mredziny.data.db.communication.BusLine
import ms.mredziny.data.db.communication.City
import ms.mredziny.data.db.communication.DayType
import ms.mredziny.data.db.communication.LatLngTypeConverter
import ms.mredziny.data.db.communication.Schedule
import ms.mredziny.data.db.communication.TransportTypeConverter
import ms.mredziny.data.db.communication.ScheduleConverter
import ms.mredziny.data.db.wastemanagement.Date
import ms.mredziny.data.db.wastemanagement.WasteSchedule
import ms.mredziny.data.news.NewsDao
import ms.mredziny.data.news.NewsItem


@Database(
    entities = [Direction::class, BusStop::class, City::class, BusLine::class, BusStopSchedule::class, DayType::class, Schedule::class,
                WasteCollectionSchedule::class, Place::class, Street::class,WasteSchedule::class, Date::class, NewsItem::class],
                version = 6, exportSchema = true)
@TypeConverters(
    TransportTypeConverter::class,
    ScheduleConverter::class,
    WasteCollectionScheduleConverter::class,
    LatLngTypeConverter::class
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun directionDao(): DirectionDao
    abstract fun busStopDao(): BusStopDao
    abstract fun cityDao(): CityDao
    abstract fun busLineDao(): BusLineDao
    abstract fun busStopScheduleDao(): BusStopScheduleDao
    abstract fun dayTypeDao(): DayTypeDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun WasteCollectionScheduleDao(): WasteCollectionScheduleDao

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}

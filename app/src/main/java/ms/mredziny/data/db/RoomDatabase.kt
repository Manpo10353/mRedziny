package ms.mredziny.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ms.mredziny.data.db.WasteManagment.Place
import ms.mredziny.data.db.WasteManagment.Street
import ms.mredziny.data.db.WasteManagment.WasteCollectionSchedule
import ms.mredziny.data.db.WasteManagment.WasteCollectionScheduleConverter
import ms.mredziny.data.db.Communication.Direction
import ms.mredziny.data.db.Communication.DirectionConverter
import ms.mredziny.data.db.Communication.BusStop
import ms.mredziny.data.db.Communication.BusStopSchedule
import ms.mredziny.data.db.Communication.BusLine
import ms.mredziny.data.db.Communication.City
import ms.mredziny.data.db.Communication.DayType
import ms.mredziny.data.db.Communication.LatLngTypeConverter
import ms.mredziny.data.db.Communication.Schedule
import ms.mredziny.data.db.Communication.TransportTypeConverter
import ms.mredziny.data.db.Communication.ScheduleConverter
import ms.mredziny.data.db.Communication.PairConverter
import ms.mredziny.data.db.WasteManagment.Date
import ms.mredziny.data.db.WasteManagment.WasteSchedule
import ms.mredziny.data.news.NewsDao
import ms.mredziny.data.news.NewsItem


@Database(
    entities = [Direction::class, BusStop::class, City::class, BusLine::class, BusStopSchedule::class, DayType::class, Schedule::class,
                WasteCollectionSchedule::class, Place::class, Street::class,WasteSchedule::class, Date::class, NewsItem::class],
                version = 2, exportSchema = true)
@TypeConverters(
    TransportTypeConverter::class,
    ScheduleConverter::class,
    PairConverter::class,
    DirectionConverter::class,
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}

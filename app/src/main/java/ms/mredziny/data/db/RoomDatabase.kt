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
import ms.mredziny.data.db.Comunication.Direction
import ms.mredziny.data.db.Comunication.DirectionConverter
import ms.mredziny.data.db.Comunication.BusStop
import ms.mredziny.data.db.Comunication.BusStopSchedule
import ms.mredziny.data.db.Comunication.BusLine
import ms.mredziny.data.db.Comunication.City
import ms.mredziny.data.db.Comunication.DayType
import ms.mredziny.data.db.Comunication.LatLngTypeConverter
import ms.mredziny.data.db.Comunication.Schedule
import ms.mredziny.data.db.Comunication.TransportTypeConverter
import ms.mredziny.data.db.Comunication.ScheduleConverter
import ms.mredziny.data.db.Comunication.PairConverter
import ms.mredziny.data.db.WasteManagment.Date
import ms.mredziny.data.db.WasteManagment.WasteSchedule
import java.io.FileOutputStream
import java.io.IOException


@Database(
    entities = [Direction::class, BusStop::class, City::class, BusLine::class, BusStopSchedule::class, DayType::class, Schedule::class,
                WasteCollectionSchedule::class, Place::class, Street::class,WasteSchedule::class, Date::class],
                version = 1, exportSchema = true)
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
                    //.fallbackToDestructiveMigration()//ostrożnie, zawsze sprawdzić czy nie da się inaczej
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}

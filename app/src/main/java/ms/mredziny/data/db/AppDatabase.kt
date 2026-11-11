package ms.mredziny.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ms.mredziny.data.db.converter.Converters
import ms.mredziny.data.db.administrative.AdministrativeDao
import ms.mredziny.data.db.administrative.GminaEntity
import ms.mredziny.data.db.administrative.LocalityEntity
import ms.mredziny.data.db.administrative.SolectwoEntity
import ms.mredziny.data.db.communication.BusLineEntity
import ms.mredziny.data.db.communication.BusStopEntity
import ms.mredziny.data.db.communication.CommunicationDao
import ms.mredziny.data.db.communication.DepartureEntity
import ms.mredziny.data.db.communication.DirectionEntity
import ms.mredziny.data.db.communication.FavouriteStopEntity
import ms.mredziny.data.db.communication.PublicHolidayEntity
import ms.mredziny.data.sync.SyncDao
import ms.mredziny.data.sync.SyncStateEntity
import ms.mredziny.data.db.wastemanagement.StreetEntity
import ms.mredziny.data.db.wastemanagement.WasteCollectionEntity
import ms.mredziny.data.db.wastemanagement.WasteDao
import ms.mredziny.data.db.wastemanagement.WasteRegionEntity
import ms.mredziny.data.db.news.NewsDao
import ms.mredziny.data.db.news.NewsEntity

@Database(
    entities = [
        GminaEntity::class, LocalityEntity::class, SolectwoEntity::class,
        BusStopEntity::class, BusLineEntity::class, DepartureEntity::class, DirectionEntity::class,
        PublicHolidayEntity::class, FavouriteStopEntity::class,
        WasteRegionEntity::class, StreetEntity::class, WasteCollectionEntity::class,
        SyncStateEntity::class,
        NewsEntity::class,
    ],
    version = 9,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun administrativeDao(): AdministrativeDao
    abstract fun communicationDao(): CommunicationDao
    abstract fun wasteDao(): WasteDao
    abstract fun syncDao(): SyncDao
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
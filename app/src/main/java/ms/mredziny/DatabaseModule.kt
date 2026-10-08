package ms.mredziny

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ms.mredziny.data.db.AppDatabase
import ms.mredziny.data.db.BusLineDao
import ms.mredziny.data.db.BusStopDao
import ms.mredziny.data.db.Communication.BusStopRepository
import ms.mredziny.data.db.BusStopScheduleDao
import ms.mredziny.data.db.CityDao
import ms.mredziny.data.db.DayTypeDao
import ms.mredziny.data.db.DirectionDao
import ms.mredziny.data.db.ScheduleDao
import ms.mredziny.data.db.WasteCollectionScheduleDao
import ms.mredziny.data.news.NewsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getInstance(appContext)
    }

    @Provides
    fun provideBusStopDao(db: AppDatabase): BusStopDao {
        return db.busStopDao()
    }

    @Provides
    fun provideDirectionDao(db: AppDatabase): DirectionDao {
        return db.directionDao()
    }

    @Provides
    fun provideCityDao(db: AppDatabase): CityDao {
        return db.cityDao()
    }

    @Provides
    fun provideBusLineDao(db: AppDatabase): BusLineDao {
        return db.busLineDao()
    }

    @Provides
    fun provideBusStopScheduleDao(db: AppDatabase): BusStopScheduleDao {
        return db.busStopScheduleDao()
    }

    @Provides
    fun provideDayTypeDao(db: AppDatabase): DayTypeDao {
        return db.dayTypeDao()
    }

    @Provides
    fun provideScheduleDao(db: AppDatabase): ScheduleDao {
        return db.scheduleDao()
    }

    @Provides
    fun provideBusStopRepository(busStopDao: BusStopDao, directionDao: DirectionDao, cityDao: CityDao, busLineDao: BusLineDao, busStopScheduleDao: BusStopScheduleDao, dayTypeDao: DayTypeDao, scheduleDao: ScheduleDao): BusStopRepository {
        return BusStopRepository(busStopDao, directionDao, cityDao, busLineDao, busStopScheduleDao, dayTypeDao, scheduleDao)
    }

    @Provides
    fun provideWasteCollectionScheduleDao(db: AppDatabase): WasteCollectionScheduleDao {
        return db.WasteCollectionScheduleDao()
    }

    @Provides
    fun provideNewsDao(db: AppDatabase): NewsDao {
        return db.newsDao()
    }
}

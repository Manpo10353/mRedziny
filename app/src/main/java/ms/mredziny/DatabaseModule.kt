package ms.mredziny

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ms.mredziny.data.db.AppDatabase
import ms.mredziny.data.sync.SyncDao
import ms.mredziny.data.db.administrative.AdministrativeDao
import ms.mredziny.data.db.communication.CommunicationDao
import ms.mredziny.data.db.wastemanagement.WasteDao
import ms.mredziny.data.db.news.NewsDao
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
    fun provideCommunicationDao(db: AppDatabase): CommunicationDao = db.communicationDao()

    @Provides
    fun provideWasteDao(db: AppDatabase): WasteDao = db.wasteDao()

    @Provides
    fun provideAdministrativeDao(db: AppDatabase): AdministrativeDao = db.administrativeDao()

    @Provides
    fun provideSyncDao(db: AppDatabase): SyncDao = db.syncDao()

    @Provides
    fun provideNewsDao(db: AppDatabase): NewsDao = db.newsDao()
}

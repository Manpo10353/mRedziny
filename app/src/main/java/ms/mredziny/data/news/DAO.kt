package ms.mredziny.data.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // Pobiera wpis z bazy
    @Query("SELECT * FROM news_table LIMIT 1")
    fun getLatestNews(): Flow<NewsItem?>

    // Zapisuje wpis
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsItem)

    // Czyszczenie bazy przed zapisaniem najnowszych aktualności
    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()
}

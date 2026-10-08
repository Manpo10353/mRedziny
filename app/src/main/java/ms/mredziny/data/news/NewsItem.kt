package ms.mredziny.data.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsItem(
    @PrimaryKey
    val originalUrl: String,
    val title: String,
    val imageUrl: String,
    val content: String,
    val date: String? = null
)

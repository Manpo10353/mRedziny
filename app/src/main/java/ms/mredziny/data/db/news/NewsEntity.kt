package ms.mredziny.data.db.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey
    val originalUrl: String,
    val title: String,
    val imageUrl: String,
    val content: String,
    val date: String? = null
)

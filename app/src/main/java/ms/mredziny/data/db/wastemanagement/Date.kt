package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Date(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val month: String,
    val year: Int
)

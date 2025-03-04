package ms.mredziny.data.db.WasteManagment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Date(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val month: String,
    val year: Int
)

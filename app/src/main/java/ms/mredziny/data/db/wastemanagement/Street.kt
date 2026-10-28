package ms.mredziny.data.db.wastemanagement

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "street",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class Street(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val placeId: Int
)

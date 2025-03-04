package ms.mredziny.data.db.Comunication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(
    tableName = "busstop",
    foreignKeys = [
        ForeignKey(
            entity = Direction::class,
            parentColumns = ["id"],
            childColumns = ["directionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = City::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("directionId"),
        Index("cityId")
    ]
)

data class BusStop(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val busStopName: String,
    val directionId: Int,
    val cityId: Int,
    val favourite: Boolean,
    @TypeConverters(TransportTypeConverter::class) val transportTypes: List<TransportType>,
    @TypeConverters(LatLngTypeConverter::class) val location: LatLng,
)

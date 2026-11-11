package ms.mredziny.data.db.communication

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "bus_line",
    indices = [Index(value = ["name"], unique = true)]
)
data class BusLineEntity(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("operator")
    val operator: String = "",

    @SerialName("description")
    val description: String? = null
)
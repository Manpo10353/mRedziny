package ms.mredziny.data.db.administrative

import androidx.room.Entity
import androidx.room.PrimaryKey

// latitude i longitude to punkt reprezentacyjny z Supabase (nullable, bo może go jeszcze nie być).
@Entity(tableName = "gmina")
data class GminaEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val teryt: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

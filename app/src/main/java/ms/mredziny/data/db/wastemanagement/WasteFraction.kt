package ms.mredziny.data.db.wastemanagement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// dbValue to wartość z bazy, label to nazwa worka, color to kolor w kalendarzu (ARGB).
@Serializable
enum class WasteFraction(val dbValue: String, val label: String, val color: Long) {
    @SerialName("mixed") MIXED("mixed", "Odpady zmieszane", 0xFF241F1FL),
    @SerialName("yellow_blue") YELLOW_BLUE("yellow_blue", "Worek żółty i niebieski", 0xFFFEB511L),
    @SerialName("glass") GLASS("glass", "Szkło (worek zielony)", 0xFF21810CL),
    @SerialName("bio") BIO("bio", "Bio (worek brązowy)", 0xFF7E4017L),
    @SerialName("ash") ASH("ash", "Popiół", 0xFF9E9E9EL);

    companion object {
        fun fromDbValue(value: String): WasteFraction? = entries.firstOrNull { it.dbValue == value }
    }
}

package ms.mredziny.data.db.communication

data class DepartureRow(
    val time: String,
    val line: String,
    val operator: String,
    val description: String? = null,
)

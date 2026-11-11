package ms.mredziny.model

data class BusStop(
    val id: Long,
    val name: String,
    val directionName: String,
    val city: String,
    val location: Coordinates,
    val isFavourite: Boolean,
    val operators: Set<String>,
)

package ms.mredziny.model

// Własny typ współrzędnych. Warstwa danych nie zależy od Google Maps.
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)

package ms.mredziny.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Lustro odpowiedzi Supabase (snake_case → camelCase przez @SerialName).

@Serializable
data class DataVersionDto(
    val dataset: String,
    val version: Long,
)

@Serializable
data class GminaDto(
    val id: Long,
    val name: String,
    val teryt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class LocalityDto(
    val id: Long,
    @SerialName("gmina_id") val gminaId: Long,
    val name: String,
    val simc: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class SolectwoDto(
    val id: Long,
    @SerialName("locality_id") val localityId: Long,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class DirectionDto(
    val id: Long,
    val name: String,
)

@Serializable
data class BusStopDto(
    val id: Long,
    val name: String,
    @SerialName("direction_name") val directionName: String,
    val city: String,
    @SerialName("direction_id") val directionId: Long? = null,
    @SerialName("locality_id") val localityId: Long? = null,
    val latitude: Double,
    val longitude: Double,
    @SerialName("timetable_valid_from") val timetableValidFrom: String? = null,
)

@Serializable
data class BusLineDto(
    val id: Long,
    val name: String,
    val operator: String,
    val description: String? = null,
)

@Serializable
data class DepartureDto(
    val id: Long,
    @SerialName("bus_stop_id") val busStopId: Long,
    @SerialName("bus_line_id") val busLineId: Long,
    @SerialName("day_type") val dayType: String,
    @SerialName("departure_time") val departureTime: String,
)

@Serializable
data class PublicHolidayDto(
    @SerialName("holiday_date") val holidayDate: String,
    val name: String,
)

@Serializable
data class WasteRegionDto(
    val id: Long,
    val name: String,
)

@Serializable
data class StreetDto(
    val id: Long,
    @SerialName("locality_id") val localityId: Long,
    @SerialName("waste_region_id") val wasteRegionId: Long,
    val name: String,
)

@Serializable
data class WasteCollectionDto(
    val id: Long,
    @SerialName("waste_region_id") val wasteRegionId: Long,
    val fraction: String,
    @SerialName("collection_date") val collectionDate: String,
)

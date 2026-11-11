package ms.mredziny.data.remote

import ms.mredziny.data.db.administrative.*
import ms.mredziny.data.db.communication.*
import ms.mredziny.data.db.wastemanagement.*
import ms.mredziny.data.remote.dto.*

fun GminaDto.toEntity() = GminaEntity(
    id = id,
    name = name,
    teryt = teryt,
    latitude = latitude,
    longitude = longitude,
)

fun LocalityDto.toEntity() = LocalityEntity(
    id = id,
    gminaId = gminaId,
    name = name,
    simc = simc,
    latitude = latitude,
    longitude = longitude,
)

fun SolectwoDto.toEntity() = SolectwoEntity(
    id = id,
    localityId = localityId,
    name = name,
    latitude = latitude,
    longitude = longitude,
)

fun DirectionDto.toEntity() = DirectionEntity(id, name)

fun BusStopDto.toEntity() = BusStopEntity(
    id = id,
    name = name,
    directionName = directionName,
    city = city,
    latitude = latitude,
    longitude = longitude,
    timetableValidFrom = timetableValidFrom,
    directionId = directionId,
    localityId = localityId,
)

fun BusLineDto.toEntity() = BusLineEntity(id, name, operator, description)

fun DepartureDto.toEntity() = DepartureEntity(
    id, busStopId, busLineId, dayType, departureTime.take(5), // "17:22:00" → "17:22"
)

fun PublicHolidayDto.toEntity() = PublicHolidayEntity(holidayDate, name)

fun WasteRegionDto.toEntity() = WasteRegionEntity(id, name)
fun StreetDto.toEntity() = StreetEntity(id, localityId, wasteRegionId, name)
fun WasteCollectionDto.toEntity() = WasteCollectionEntity(id, wasteRegionId, fraction, collectionDate)

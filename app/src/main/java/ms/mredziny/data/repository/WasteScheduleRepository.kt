package ms.mredziny.data.repository

import ms.mredziny.data.db.wastemanagement.WasteDao
import ms.mredziny.data.db.wastemanagement.WasteFraction
import ms.mredziny.model.Place
import ms.mredziny.model.Street
import ms.mredziny.model.WasteSchedule
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WasteScheduleRepository @Inject constructor(
    private val dao: WasteDao,
) {
    suspend fun places(): List<Place> =
        dao.localitiesWithStreets().map { Place(it.id, it.name) }

    suspend fun streets(placeId: Long): List<Street> =
        dao.streetsByLocality(placeId).map { Street(it.id, it.name) }

    suspend fun availableMonths(): List<YearMonth> =
        dao.availableMonths().map { YearMonth.parse(it.ym) }

    // Zwraca null, gdy ulica nie ma przypisanego regionu odbioru.
    suspend fun schedule(streetId: Long, month: YearMonth): WasteSchedule? {
        val regionId = dao.regionIdForStreet(streetId) ?: return null
        val rows = dao.collections(
            regionId = regionId,
            from = month.atDay(1).toString(),
            to = month.plusMonths(1).atDay(1).toString(),
        )
        val days = rows
            .mapNotNull { row ->
                WasteFraction.fromDbValue(row.fraction)?.let { LocalDate.parse(row.date) to it }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, fractions) -> fractions.toSet() }
        return WasteSchedule(month, days)
    }
}

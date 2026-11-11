package ms.mredziny.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import jakarta.inject.Inject
import ms.mredziny.data.db.AppDatabase
import ms.mredziny.data.remote.dto.*
import ms.mredziny.data.remote.toEntity

private const val PAGE = 1000L

class TimetableSync @Inject constructor(
    private val supabase: SupabaseClient,
    private val db: AppDatabase,
) {
    suspend fun syncIfNeeded() {
        val remote = supabase.from("data_version")
            .select()
            .decodeList<DataVersionDto>()

        val local = db.syncDao().all().associate { it.dataset to it.version }

        remote.forEach { row ->
            if (local[row.dataset] != row.version) {
                pull(row.dataset)
                db.syncDao().upsert(SyncStateEntity(row.dataset, row.version))
            }
        }
    }

    // Nazwy zgodne z tabelą data_version: bus, borders, waste.
    private suspend fun pull(dataset: String) {
        when (dataset) {
            "bus" -> pullBus()
            "borders" -> pullBorders()
            "waste" -> pullWaste()
        }
    }

    private suspend fun pullBus() {
        val stops = supabase.fetchAll<BusStopDto>("bus_stop").map { it.toEntity() }
        val lines = supabase.fetchAll<BusLineDto>("bus_line").map { it.toEntity() }
        val departures = supabase.fetchAll<DepartureDto>("departure").map { it.toEntity() }
        val holidays = supabase.fetchAll<PublicHolidayDto>("public_holiday", orderBy = "holiday_date").map { it.toEntity() }
        val directions = supabase.fetchAll<DirectionDto>("direction").map { it.toEntity() }

        db.communicationDao().replaceAll(stops, lines, departures, holidays, directions)
    }

    private suspend fun pullBorders() {
        val gminas = supabase.fetchAll<GminaDto>("gmina").map { it.toEntity() }
        val localities = supabase.fetchAll<LocalityDto>("locality").map { it.toEntity() }
        val solectwa = supabase.fetchAll<SolectwoDto>("solectwo").map { it.toEntity() }

        db.administrativeDao().replaceAll(gminas, localities, solectwa)
    }

    private suspend fun pullWaste() {
        val regions = supabase.fetchAll<WasteRegionDto>("waste_region").map { it.toEntity() }
        val streets = supabase.fetchAll<StreetDto>("street").map { it.toEntity() }
        val collections = supabase.fetchAll<WasteCollectionDto>("waste_collection").map { it.toEntity() }

        db.wasteDao().replaceAll(regions, streets, collections)
    }

    private suspend inline fun <reified T : Any> SupabaseClient.fetchAll(
        table: String,
        orderBy: String = "id",
    ): List<T> {
        val out = mutableListOf<T>()
        var offset = 0L
        while (true) {
            val page = from(table)
                .select {
                    order(orderBy, Order.ASCENDING)
                    range(offset, offset + PAGE - 1)
                }
                .decodeList<T>()
            out += page
            if (page.size < PAGE) break
            offset += PAGE
        }
        return out
    }
}

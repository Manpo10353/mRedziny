package ms.mredziny.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ms.mredziny.data.db.communication.BusStopEntity
import ms.mredziny.data.db.communication.CommunicationDao
import ms.mredziny.data.db.communication.FavouriteStopEntity
import ms.mredziny.model.BusStop
import ms.mredziny.model.Coordinates
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusStopRepository @Inject constructor(
    private val dao: CommunicationDao,
) {
    // Obserwuje przystanki i ulubione, więc odświeża się po synchronizacji oraz zmianie ulubionych.
    fun stopsByDirection(direction: String): Flow<List<BusStop>> =
        combine(dao.stopsByDirection(direction), dao.favourites()) { stops, favourites ->
            loadStops(stops, favourites.toIds())
        }

    fun favouriteStopsByDirection(direction: String): Flow<List<BusStop>> =
        stopsByDirection(direction).map { stops -> stops.filter { it.isFavourite } }

    suspend fun allStops(): List<BusStop> =
        loadStops(dao.allStops(), currentFavouriteIds())

    suspend fun stopById(stopId: Long): BusStop? =
        dao.stopById(stopId)?.let { loadStops(listOf(it), currentFavouriteIds()).first() }

    suspend fun setFavourite(stopId: Long, favourite: Boolean) {
        if (favourite) {
            dao.addFavourite(FavouriteStopEntity(stopId))
        } else {
            dao.removeFavourite(stopId)
        }
    }

    private suspend fun currentFavouriteIds(): Set<Long> = dao.favourites().first().toIds()

    private fun List<FavouriteStopEntity>.toIds(): Set<Long> = mapTo(HashSet()) { it.stopId }

    private suspend fun loadStops(entities: List<BusStopEntity>, favouriteIds: Set<Long>): List<BusStop> {
        val operatorsByStop = dao.stopOperators().groupBy({ it.stopId }, { it.operator })
        return entities.map { entity ->
            BusStop(
                id = entity.id,
                name = entity.name,
                directionName = entity.directionName,
                city = entity.city,
                location = Coordinates(entity.latitude, entity.longitude),
                isFavourite = entity.id in favouriteIds,
                operators = operatorsByStop[entity.id].orEmpty().toSet(),
            )
        }
    }
}

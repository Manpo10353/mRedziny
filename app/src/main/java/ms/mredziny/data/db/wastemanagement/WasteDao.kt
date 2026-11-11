package ms.mredziny.data.db.wastemanagement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class WasteDao {

    @Query(
        """
        SELECT c.collectionDate AS date, c.fraction AS fraction
        FROM waste_collection c
        WHERE c.wasteRegionId = :regionId
          AND c.collectionDate >= :from
          AND c.collectionDate < :to
        ORDER BY c.collectionDate
        """
    )
    abstract suspend fun collections(regionId: Long, from: String, to: String): List<CollectionRow>

    @Query("SELECT wasteRegionId FROM street WHERE id = :streetId")
    abstract suspend fun regionIdForStreet(streetId: Long): Long?

    // Tylko miejscowości, w których są ulice z harmonogramem.
    @Query(
        """
        SELECT DISTINCT l.id AS id, l.name AS name
        FROM locality l
        JOIN street s ON s.localityId = l.id
        ORDER BY l.name
        """
    )
    abstract suspend fun localitiesWithStreets(): List<IdNameRow>

    @Query("SELECT id AS id, name AS name FROM street WHERE localityId = :localityId ORDER BY name")
    abstract suspend fun streetsByLocality(localityId: Long): List<IdNameRow>

    @Query("SELECT DISTINCT substr(collectionDate, 1, 7) AS ym FROM waste_collection ORDER BY ym")
    abstract suspend fun availableMonths(): List<YearMonthRow>

    @Transaction
    open suspend fun replaceAll(
        regions: List<WasteRegionEntity>,
        streets: List<StreetEntity>,
        collections: List<WasteCollectionEntity>,
    ) {
        clearCollections()
        clearStreets()
        clearRegions()
        insertRegions(regions)
        insertStreets(streets)
        insertCollections(collections)
    }

    @Query("DELETE FROM waste_collection") abstract suspend fun clearCollections()
    @Query("DELETE FROM street") abstract suspend fun clearStreets()
    @Query("DELETE FROM waste_region") abstract suspend fun clearRegions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRegions(items: List<WasteRegionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertStreets(items: List<StreetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCollections(items: List<WasteCollectionEntity>)
}
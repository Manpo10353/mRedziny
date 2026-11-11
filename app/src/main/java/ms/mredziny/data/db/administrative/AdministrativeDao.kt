package ms.mredziny.data.db.administrative

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class AdministrativeDao {

    @Query("SELECT * FROM gmina ORDER BY name")
    abstract suspend fun gminas(): List<GminaEntity>

    @Query("SELECT * FROM locality WHERE gminaId = :gminaId ORDER BY name")
    abstract suspend fun localities(gminaId: Long): List<LocalityEntity>

    @Query("SELECT * FROM solectwo WHERE localityId = :localityId ORDER BY name")
    abstract suspend fun solectwa(localityId: Long): List<SolectwoEntity>

    @Transaction
    open suspend fun replaceAll(
        gminas: List<GminaEntity>,
        localities: List<LocalityEntity>,
        solectwa: List<SolectwoEntity>,
    ) {
        clearSolectwa()
        clearLocalities()
        clearGminas()
        insertGminas(gminas)
        insertLocalities(localities)
        insertSolectwa(solectwa)
    }

    @Query("DELETE FROM solectwo") abstract suspend fun clearSolectwa()
    @Query("DELETE FROM locality") abstract suspend fun clearLocalities()
    @Query("DELETE FROM gmina") abstract suspend fun clearGminas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGminas(items: List<GminaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLocalities(items: List<LocalityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSolectwa(items: List<SolectwoEntity>)
}
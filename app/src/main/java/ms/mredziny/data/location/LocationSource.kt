package ms.mredziny.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun current(): Location? =
        client.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token,
        ).await()

    /** Emits a fresh fix immediately, then balanced-accuracy updates while collected. */
    @SuppressLint("MissingPermission")
    fun updates(intervalMillis: Long = 30_000L): Flow<Location> = callbackFlow {
        val cancellation = CancellationTokenSource()
        var newestTimestamp = 0L

        fun emitIfNewer(location: Location?) {
            if (location != null && location.time >= newestTimestamp) {
                newestTimestamp = location.time
                trySend(location)
            }
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                emitIfNewer(result.lastLocation)
            }
        }

        try {
            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellation.token,
            ).addOnSuccessListener(::emitIfNewer)
                .addOnFailureListener { error -> close(error) }

            val request = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                intervalMillis,
            )
                .setMinUpdateIntervalMillis(intervalMillis / 2)
                .build()

            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                .addOnFailureListener { error -> close(error) }
        } catch (error: SecurityException) {
            close(error)
        }

        awaitClose {
            cancellation.cancel()
            client.removeLocationUpdates(callback)
        }
    }.conflate()
}

package ms.mredziny.viewModel

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.mredziny.data.db.communication.BusStopRepository
import ms.mredziny.ui.comunication.busLineMap.BusLineMapUiState
import javax.inject.Inject

@HiltViewModel
class BusLineMapViewModel @Inject constructor(private val repository: BusStopRepository) : ViewModel(){

    private val _uiState = MutableStateFlow<BusLineMapUiState>(BusLineMapUiState.Idle())
    val uiState: StateFlow<BusLineMapUiState> = _uiState.asStateFlow()

    fun getAllBusStops(viewPoint: LatLng) {
        viewModelScope.launch {
        _uiState.value = when(val currentState = _uiState.value){
            is BusLineMapUiState.Idle -> {val busStopList = repository.getAllBusStops()
                BusLineMapUiState.Success(
                    busStopList = busStopList,
                    viewPoint = viewPoint
                )
            }
            else ->
                return@launch
        }
        }
    }
    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // Load the vector drawable
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

        // Set bounds for the drawable
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

        // Create a bitmap with the correct dimensions
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the bitmap
        val canvas = android.graphics.Canvas(bitmap)

        // Draw the vector drawable onto the canvas
        vectorDrawable.draw(canvas)

        // Create and return the BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}

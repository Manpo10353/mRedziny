package ms.mredziny.ui.comunication.busLineMap

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ms.mredziny.R
import ms.mredziny.data.db.Comunication.BusStop
import ms.mredziny.viewModel.BusLineMapViewModel

@Composable
fun BusLineMapScreen(navController: NavHostController, viewModel: BusLineMapViewModel, viewPoint: LatLng, zoom: Float){
    LaunchedEffect(Unit) {
        viewModel.getAllBusStops(viewPoint)
    }
    val uiState = viewModel.uiState.collectAsState()
    val uiStateValue = uiState.value

    val mapView = rememberMapViewWithLifecycle()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (selectedBusStop, setSelectedBusStop) = remember { mutableStateOf<BusStop?>(null) }

    AndroidView({ mapView }) { mapView ->
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            if (uiStateValue is BusLineMapUiState.Success) {
                val viewPoint = uiStateValue.viewPoint
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewPoint, zoom))
            }

            //val descriptor = viewModel.bitmapDescriptorFromVector(context, R.drawable.directions_bus_48px)

            when (val uiState = viewModel.uiState.value) {
                is BusLineMapUiState.Success -> {
                    uiState.busStopList.forEach { busStop ->
                        val point = busStop.location
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(point)
                                //.icon(bitmapDescriptor)
                        )
                        // Attach the busStop object to the marker for later retrieval
                        marker?.tag = busStop
                    }
                }
                else -> {}
            }

            // Set a listener that will be triggered when a marker is clicked

            googleMap.setOnMarkerClickListener { marker ->
                val busStop = marker.tag as BusStop
                setSelectedBusStop(busStop)
                setShowDialog(true)
                true // Return true to indicate that we have handled the event
            }
        }
    }
    if (showDialog && selectedBusStop != null) {
        ShowDialog(
            selectedBusStop,
            onDismiss = {setShowDialog(false) }
        )
    }
}

@Composable
fun ShowDialog(busStop: BusStop, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = { Text(busStop.busStopName) },
        text = { Text("") },
        confirmButton = {
            Button(onClick = { onDismiss()}) {
                Text("Zakmnij")
            }
        }
    )
}


@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = getMapViewLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

fun getMapViewLifecycleObserver(mapView: MapView): DefaultLifecycleObserver {
    return object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            mapView.onCreate(null)
        }

        override fun onStart(owner: LifecycleOwner) {
            mapView.onStart()
        }

        override fun onResume(owner: LifecycleOwner) {
            mapView.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            mapView.onPause()
        }

        override fun onStop(owner: LifecycleOwner) {
            mapView.onStop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            mapView.onDestroy()
        }
    }
}




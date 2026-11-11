package ms.mredziny.feature.communication.linemap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.DepartureBoard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ms.mredziny.R
import ms.mredziny.model.BusStop
import ms.mredziny.navigateToBusStopSchedule
import ms.mredziny.ui.theme.Gold

@Composable
fun BusLineMapScreen(
    navController: NavHostController,
    viewModel: BusLineMapViewModel,
    viewPoint: LatLng,
    zoom: Float,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    var stopMarkerIcons by remember { mutableStateOf<StopMarkerIcons?>(null) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var selectedStopId by remember { mutableStateOf<Long?>(null) }
    val selectedStop = state.stops.firstOrNull { it.id == selectedStopId }

    // Ustawiamy kamerę raz, żeby nie przeskakiwała przy odświeżaniu przystanków.
    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            map.uiSettings.isZoomControlsEnabled = true
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(viewPoint, zoom))
            map.setOnMarkerClickListener { marker ->
                selectedStopId = (marker.tag as? BusStop)?.id
                true
            }
            map.setOnMapClickListener {
                selectedStopId = null
            }
            googleMap = map
        }
    }

    // Przy wejściu z rozkładu od razu pokazujemy kartę przystanku wskazanego współrzędnymi.
    LaunchedEffect(state.stops, viewPoint, zoom) {
        if (selectedStopId == null && zoom > 15f) {
            selectedStopId = state.stops.firstOrNull { stop ->
                kotlin.math.abs(stop.location.latitude - viewPoint.latitude) < 0.000001 &&
                    kotlin.math.abs(stop.location.longitude - viewPoint.longitude) < 0.000001
            }?.id
        }
    }

    // Markery odświeżamy przy zmianie listy lub ulubionego przystanku.
    LaunchedEffect(googleMap, state.stops, selectedStopId) {
        val map = googleMap ?: return@LaunchedEffect
        val icons = stopMarkerIcons ?: StopMarkerIcons(
            normal = createStopMarkerIcon(context, selected = false),
            selected = createStopMarkerIcon(context, selected = true),
        ).also { stopMarkerIcons = it }
        map.clear()
        state.stops.forEach { stop ->
            val isSelected = stop.id == selectedStopId
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.location.latitude, stop.location.longitude))
                    .title(stop.name)
                    .icon(if (isSelected) icons.selected else icons.normal)
                    .anchor(0.5f, 0.5f)
                    .zIndex(if (isSelected) 1f else 0f),
            )
            marker?.tag = stop
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        selectedStop?.let { stop ->
            StopMapCard(
                stop = stop,
                onScheduleClick = {
                    navController.navigateToBusStopSchedule(stop.id, stop.name, stop.directionName)
                },
                onFavouriteClick = { viewModel.toggleFavourite(stop) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun StopMapCard(
    stop: BusStop,
    onScheduleClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 36.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(50),
                    ),
            )

            val subtitle = listOf(stop.city, stop.operators.sorted().joinToString(" · "))
                .filter { it.isNotBlank() }
                .joinToString(" · ")
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stop.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onScheduleClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DepartureBoard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Pokaż rozkład")
                }
                IconButton(
                    onClick = onFavouriteClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp),
                        ),
                ) {
                    Icon(
                        imageVector = if (stop.isFavourite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (stop.isFavourite) {
                            "Usuń z ulubionych"
                        } else {
                            "Dodaj do ulubionych"
                        },
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private data class StopMarkerIcons(
    val normal: BitmapDescriptor,
    val selected: BitmapDescriptor,
)

private fun createStopMarkerIcon(context: Context, selected: Boolean): BitmapDescriptor {
    val density = context.resources.displayMetrics.density
    val sizeDp = if (selected) 44 else 36
    val sizePx = (sizeDp * density).toInt().coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = sizePx / 2f

    val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (selected) Gold.toArgb() else AndroidColor.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(center, center, sizePx * 0.46f, outlinePaint)

    if (selected) {
        val innerOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(center, center, sizePx * 0.36f, innerOutlinePaint)
    }

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(10, 74, 133)
        style = Paint.Style.FILL
    }
    canvas.drawCircle(center, center, sizePx * if (selected) 0.26f else 0.36f, fillPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply { id = R.id.map }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val observer = mapViewLifecycleObserver(mapView)
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return mapView
}

private fun mapViewLifecycleObserver(mapView: MapView): DefaultLifecycleObserver =
    object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(null)
        override fun onStart(owner: LifecycleOwner) = mapView.onStart()
        override fun onResume(owner: LifecycleOwner) = mapView.onResume()
        override fun onPause(owner: LifecycleOwner) = mapView.onPause()
        override fun onStop(owner: LifecycleOwner) = mapView.onStop()
        override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
    }

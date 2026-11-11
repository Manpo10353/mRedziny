package ms.mredziny.feature.communication.favourite

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.catch
import ms.mredziny.feature.communication.common.HeaderBox
import ms.mredziny.feature.communication.common.StopCard
import ms.mredziny.model.BusStop
import ms.mredziny.model.Coordinates
import ms.mredziny.navigateToBusStopSchedule
import ms.mredziny.ui.theme.MredzinyTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BusFavouriteScreen(navController: NavHostController, viewModel: BusFavouriteViewModel, direction: String) {
    LaunchedEffect(direction) { viewModel.setDirection(direction) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    val hasLocationPermission = locationPermissionsState.permissions.any { it.status.isGranted }

    LaunchedEffect(hasLocationPermission, lifecycleOwner) {
        if (!hasLocationPermission) {
            viewModel.clearUserLocation()
            return@LaunchedEffect
        }

        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.locationUpdates()
                .catch { viewModel.clearUserLocation() }
                .collect(viewModel::updateUserLocation)
        }
    }

    BusFavouriteContent(
        direction = direction,
        state = state,
        hasLocationPermission = hasLocationPermission,
        onSearchClick = { navController.navigate("bus_stops/${Uri.encode(direction)}") },
        onRequestLocationPermission = { locationPermissionsState.launchMultiplePermissionRequest() },
        onStopClick = { stop ->
            navController.navigateToBusStopSchedule(stop.id, stop.name, direction)
        },
        onFavouriteClick = viewModel::toggleFavourite,
    )
}

@Composable
private fun BusFavouriteContent(
    direction: String,
    state: BusFavouriteUiState,
    hasLocationPermission: Boolean,
    onSearchClick: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onStopClick: (BusStop) -> Unit,
    onFavouriteClick: (BusStop) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            HeaderBox(title = "Kierunek $direction")

            ElevatedButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Wyszukaj przystanek",
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wyszukaj przystanek",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Text(
                text = "Najbliższy przystanek",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )

            val nearestInfo = state.nearestStopInfo
            if (nearestInfo != null) {
                StopCard(
                    stop = nearestInfo.stop,
                    distanceFormatted = nearestInfo.distanceFormatted,
                    onClick = { onStopClick(nearestInfo.stop) },
                    onFavouriteClick = { onFavouriteClick(nearestInfo.stop) },
                )
            } else if (!hasLocationPermission) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Włącz lokalizację, aby odnaleźć najbliższy przystanek",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(onClick = onRequestLocationPermission) {
                            Text("Włącz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Wyszukiwanie najbliższego przystanku...",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Text(
                text = "Ulubione przystanki",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val error = state.error
                when {
                    error != null -> Text("Błąd: $error", modifier = Modifier.align(Alignment.Center))
                    state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    state.favourites.isEmpty() -> Text(
                        text = "Brak ulubionych przystanków w tym kierunku",
                        modifier = Modifier.align(Alignment.Center),
                    )
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.favourites, key = { it.id }) { stop ->
                            StopCard(
                                stop = stop,
                                onClick = { onStopClick(stop) },
                                onFavouriteClick = { onFavouriteClick(stop) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BusFavouriteScreenPreview() {
    val nearest = BusStop(
        id = 1,
        name = "Latosówka Spacerowa",
        directionName = "Rędziny",
        city = "Konin",
        location = Coordinates(50.827, 19.123),
        isFavourite = false,
        operators = setOf("GZK Rędziny"),
    )
    val favourites = listOf(
        BusStop(
            id = 2,
            name = "Strefa",
            directionName = "Rędziny",
            city = "Rędziny",
            location = Coordinates(50.832, 19.130),
            isFavourite = true,
            operators = setOf("GZK Rędziny"),
        ),
        BusStop(
            id = 3,
            name = "Bloki",
            directionName = "Rędziny",
            city = "Marianka Rędzińska",
            location = Coordinates(50.835, 19.135),
            isFavourite = true,
            operators = setOf("GZK Rędziny"),
        ),
    )

    MredzinyTheme(darkTheme = true) {
        BusFavouriteContent(
            direction = "Rędziny",
            state = BusFavouriteUiState(
                direction = "Rędziny",
                favourites = favourites,
                nearestStopInfo = NearestStopInfo(
                    stop = nearest,
                    distanceFormatted = "1,2 km",
                    distanceMeters = 1200f,
                ),
                isLoading = false,
            ),
            hasLocationPermission = false,
            onSearchClick = {},
            onRequestLocationPermission = {},
            onStopClick = {},
            onFavouriteClick = {},
        )
    }
}

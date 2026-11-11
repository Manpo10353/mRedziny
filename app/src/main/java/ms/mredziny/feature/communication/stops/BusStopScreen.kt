package ms.mredziny.feature.communication.stops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.awaitCancellation
import ms.mredziny.feature.communication.common.HeaderBox
import ms.mredziny.feature.communication.common.StopCard
import ms.mredziny.navigateToBusStopSchedule

@Composable
fun BusStopScreen(
    navController: NavHostController,
    viewModel: BusStopViewModel,
    direction: String,
) {
    LaunchedEffect(direction) {
        viewModel.setDirection(direction)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.syncIfNeeded()
            awaitCancellation()
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf(state.query) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        HeaderBox(title = "Kierunek $direction")

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                searchQuery = newText
                viewModel.updateQuery(newText)
            },
            label = { Text("Wyszukaj przystanek") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when {
                state.error != null -> {
                    Text(
                        text = "Błąd: ${state.error}",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.stops, key = { it.id }) { stop ->
                            StopCard(
                                stop = stop,
                                onClick = {
                                    navController.navigateToBusStopSchedule(
                                        stop.id,
                                        stop.name,
                                        direction,
                                    )
                                },
                                onFavouriteClick = {
                                    viewModel.toggleFavourite(stop)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

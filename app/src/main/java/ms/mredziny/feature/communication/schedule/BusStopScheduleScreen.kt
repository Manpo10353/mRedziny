package ms.mredziny.feature.communication.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ms.mredziny.Screen
import ms.mredziny.data.db.communication.DayType
import ms.mredziny.model.Coordinates
import ms.mredziny.model.Schedule
import ms.mredziny.model.TimeT
import ms.mredziny.ui.theme.LineR
import ms.mredziny.ui.theme.LineRL
import ms.mredziny.ui.theme.LineRk
import ms.mredziny.ui.theme.LineRm
import ms.mredziny.ui.theme.LineS
import ms.mredziny.ui.theme.MredzinyTheme
import java.time.LocalTime

@Composable
fun BusStopScheduleScreen(
    viewModel: BusStopScheduleViewModel,
    navController: NavHostController,
    busStopName: String,
    busStopId: Long,
    direction: String,
) {
    LaunchedEffect(busStopId, busStopName, direction) {
        viewModel.open(busStopId, busStopName, direction)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BusStopScheduleContent(
        state = state,
        busStopName = busStopName,
        direction = direction,
        onSelectDayType = viewModel::selectDayType,
        onShowMapClick = { location ->
            navController.navigate(
                "${Screen.BusLineMap.route}/${location.latitude},${location.longitude}",
            )
        },
    )
}

@Composable
fun BusStopScheduleContent(
    state: BusStopScheduleUiState,
    busStopName: String,
    direction: String,
    onSelectDayType: (DayType) -> Unit,
    onShowMapClick: (Coordinates) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    var showRouteLegend by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val time = LocalTime.now()
            val millisToNextMinute = 60_000L - time.second * 1_000L - time.nano / 1_000_000L
            delay(millisToNextMinute.coerceAtLeast(1L))
            currentTime = LocalTime.now()
        }
    }

    val schedule = state.schedule
    val routeDescriptions = schedule?.routeDescriptions().orEmpty()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Column {
                        Text(
                            text = "KIERUNEK",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = direction,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    //HorizontalDivider()
                    Column {
                        Text(
                            text = "PRZYSTANEK",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = busStopName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    state.location?.let { location ->
                        Button(
                            onClick = { onShowMapClick(location) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Map,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            //Spacer(Modifier.width(8.dp))
                            Text("Pokaż przystanek na mapie")
                        }
                    }
                }
            }

            state.request?.let { request ->
                DayTypeRow(
                    selected = request.dayType,
                    onSelect = onSelectDayType,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when {
                    state.error != null -> Text(
                        text = state.error.orEmpty(),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )

                    state.isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )

                    schedule != null -> Column(modifier = Modifier.fillMaxSize()) {
                        NextDepartureCard(
                            departure = schedule.nextDeparture(currentTime),
                            currentTime = currentTime,
                        )

                        if (routeDescriptions.isNotEmpty()) {
                            TextButton(
                                onClick = { showRouteLegend = true },
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Text("Objaśnienie oznaczeń")
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        ScheduleList(
                            schedule = schedule,
                            currentHour = currentTime.hour,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    if (showRouteLegend && routeDescriptions.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showRouteLegend = false },
            title = { Text("Oznaczenia kursów") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    routeDescriptions.forEach { (code, description) ->
                        Column {
                            Text(code, fontWeight = FontWeight.Bold)
                            Text(description)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRouteLegend = false }) {
                    Text("Zamknij")
                }
            },
        )
    }
}

@Composable
fun DayTypeRow(selected: DayType?, onSelect: (DayType) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DayType.entries.forEach { type ->
            FilterChip(
                selected = type == selected,
                onClick = { onSelect(type) },
                label = {
                    Text(
                        text = type.label,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                modifier = Modifier.height(60.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        }
    }
}

@Composable
private fun NextDepartureCard(departure: ScheduledDeparture?, currentTime: LocalTime) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        if (departure == null) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "NAJBLIŻSZY ODJAZD · WG ROZKŁADU",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text("Brak kolejnych odjazdów po bieżącej godzinie.")
            }
        } else {
            Text(
                text = "NAJBLIŻSZY ODJAZD · WG ROZKŁADU",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top=8.dp,
                    start = 16.dp,
                    end = 16.dp)
            )
            //Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = departure.formattedTime,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(8.dp))
                        RouteCodeChip(departure.time.annotation)
                    }
                }
                Text(
                    text = departure.minutesUntil(currentTime).let { minutes ->
                        if (minutes == 0) "teraz" else "za $minutes min"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                )

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScheduleList(
    schedule: Schedule,
    currentHour: Int,
    modifier: Modifier = Modifier,
) {
    val hours = remember(schedule) { schedule.times.toSortedMap().entries.toList() }
    val listState = rememberLazyListState()

    LaunchedEffect(hours, currentHour) {
        if (hours.isNotEmpty()) {
            val firstCurrentOrLaterHour = hours.indexOfFirst { it.key >= currentHour }
            val targetIndex = firstCurrentOrLaterHour.takeIf { it >= 0 } ?: hours.lastIndex
            listState.animateScrollToItem(targetIndex)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(hours, key = { it.key }) { (hour, departures) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(42.dp),
                    )
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        departures.forEach { departure ->
                            DepartureChip(departure)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DepartureChip(departure: TimeT) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = departure.minute.toString().padStart(2, '0'),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .background(
                    color = routeColor(departure.annotation),
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 5.dp, vertical = 2.dp),
        ) {
            Text(
                text = departure.annotation,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RouteCodeChip(code: String) {
    Box(
        modifier = Modifier
            .background(routeColor(code), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun routeColor(code: String): Color {
    val colorCode = code
        .trim()
        .removeSuffix("*")
        .trimEnd()
        .substringAfterLast('/')
        .substringAfterLast('-')
        .trim()

    return when (colorCode) {
        "Rk" -> LineRk
        "R" -> LineR
        "Rm" -> LineRm
        "RL" -> LineRL
        "S" -> LineS
        else -> Color(0xFF455A64)
    }
}

private data class ScheduledDeparture(
    val hour: Int,
    val time: TimeT,
) {
    val minutesOfDay: Int = hour * 60 + time.minute
    val formattedTime: String = "${hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"

    fun minutesUntil(currentTime: LocalTime): Int {
        val secondsUntil = minutesOfDay * 60 - currentTime.toSecondOfDay()
        return if (secondsUntil <= 0) 0 else (secondsUntil + 59) / 60
    }
}

private fun Schedule.nextDeparture(currentTime: LocalTime): ScheduledDeparture? {
    val currentSecond = currentTime.toSecondOfDay()
    return times
        .flatMap { (hour, departures) -> departures.map { ScheduledDeparture(hour, it) } }
        .filter { it.minutesOfDay * 60 > currentSecond }
        .minByOrNull { it.minutesOfDay }
}

private fun Schedule.routeDescriptions(): List<Pair<String, String>> =
    times.values
        .flatten()
        .mapNotNull { departure ->
            departure.description
                ?.takeIf { it.isNotBlank() }
                ?.let { departure.annotation to it }
        }
        .distinct()
        .sortedBy { it.first }

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BusStopScheduleScreenPreview() {
    val sampleSchedule = Schedule(
        times = mapOf(
            6 to listOf(
                TimeT(minute = 15, annotation = "R", description = "Kurs do Częstochowy przez Rędziny"),
                TimeT(minute = 45, annotation = "Rk", description = "Kurs skrócony do Kościełca"),
            ),
            7 to listOf(
                TimeT(minute = 10, annotation = "R"),
                TimeT(minute = 35, annotation = "RL"),
            ),
            8 to listOf(
                TimeT(minute = 20, annotation = "S", description = "Kurs w dni nauki szkolnej"),
            ),
            14 to listOf(
                TimeT(minute = 5, annotation = "Rm"),
                TimeT(minute = 50, annotation = "R"),
            ),
        ),
    )

    val sampleState = BusStopScheduleUiState(
        request = ScheduleRequest(
            stopId = 1L,
            stopName = "Rędziny Osiedle",
            direction = "Częstochowa",
            dayType = DayType.WORKDAY_SCHOOL,
        ),
        schedule = sampleSchedule,
        location = Coordinates(50.827, 19.123),
        isLoading = false,
        error = null,
    )

    MredzinyTheme {
        BusStopScheduleContent(
            state = sampleState,
            busStopName = "Rędziny Osiedle",
            direction = "Częstochowa",
            onSelectDayType = {},
            onShowMapClick = {},
        )
    }
}

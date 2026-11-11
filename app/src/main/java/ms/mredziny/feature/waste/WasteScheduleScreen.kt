package ms.mredziny.feature.waste

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ms.mredziny.data.db.wastemanagement.WasteFraction
import ms.mredziny.model.WasteSchedule
import ms.mredziny.ui.theme.LocalWasteColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val polish: Locale = Locale.forLanguageTag("pl-PL")
private val listDateFormat = DateTimeFormatter.ofPattern("dd.MM EEEE", polish)

@Composable
fun WasteScheduleScreen(viewModel: WasteScheduleViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectField(
            label = "1. Miejscowość",
            selectedLabel = state.selectedPlace?.name.orEmpty(),
            options = state.places,
            optionLabel = { it.name },
            onSelect = viewModel::selectPlace,
        )
        SelectField(
            label = "2. Ulica",
            selectedLabel = state.selectedStreet?.name.orEmpty(),
            options = state.streets,
            optionLabel = { it.name },
            onSelect = viewModel::selectStreet,
            enabled = state.selectedPlace != null,
        )

        Text("3. Miesiąc", style = MaterialTheme.typography.titleSmall)
        MonthRow(
            months = state.months,
            selected = state.selectedMonth,
            onSelect = viewModel::selectMonth,
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Presentation.entries.forEachIndexed { index, presentation ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = Presentation.entries.size),
                    selected = state.presentation == presentation,
                    onClick = { viewModel.selectPresentation(presentation) },
                ) {
                    Text(if (presentation == Presentation.LIST) "Lista" else "Kalendarz")
                }
            }
        }

        WasteContent(state)
    }
}

@Composable
private fun WasteContent(state: WasteScheduleUiState) {
    val error = state.error
    val schedule = state.schedule
    when {
        error != null -> Text("Błąd: $error")
        state.isLoading -> Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        state.selectedStreet == null -> Text("Wybierz miejscowość i ulicę, aby zobaczyć harmonogram.")
        schedule == null || schedule.days.isEmpty() -> Text("Brak wywozów w wybranym miesiącu.")
        state.presentation == Presentation.LIST -> WasteList(schedule)
        else -> WasteCalendar(schedule)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SelectField(
    label: String,
    selectedLabel: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun MonthRow(months: List<YearMonth>, selected: YearMonth, onSelect: (YearMonth) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(months, key = { it.toString() }) { month ->
            FilterChip(
                selected = month == selected,
                onClick = { onSelect(month) },
                label = { Text(month.labelPl()) },
            )
        }
    }
}

@Composable
private fun WasteList(schedule: WasteSchedule) {
    val wasteColors = LocalWasteColors.current
    Column {
        schedule.days.toSortedMap().forEach { (date, fractions) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = date.format(listDateFormat),
                    modifier = Modifier.width(140.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fractions.sortedBy { it.ordinal }.forEach { fraction ->
                        val color = when (fraction) {
                            WasteFraction.MIXED -> wasteColors.mixed
                            WasteFraction.YELLOW_BLUE -> wasteColors.metal
                            WasteFraction.GLASS -> wasteColors.glass
                            WasteFraction.BIO -> wasteColors.bio
                            WasteFraction.ASH -> wasteColors.mixed
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = fraction.label,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
        }
    }
}

// Siatka zaczyna się od poniedziałku. Puste pola to dni przed pierwszym i po ostatnim dniu miesiąca.
@Composable
private fun WasteCalendar(schedule: WasteSchedule) {
    val month = schedule.month
    val leadingBlanks = month.atDay(1).dayOfWeek.value - 1
    val daysInMonth = month.lengthOfMonth()
    val trailingBlanks = (7 - (leadingBlanks + daysInMonth) % 7) % 7
    val cells: List<LocalDate?> = List(leadingBlanks) { null } +
        (1..daysInMonth).map { month.atDay(it) } +
        List(trailingBlanks) { null }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Ndz").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        fractions = date?.let { schedule.days[it] }.orEmpty(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate?, fractions: Set<WasteFraction>, modifier: Modifier = Modifier) {
    val wasteColors = LocalWasteColors.current
    Column(
        modifier = modifier
            .height(56.dp)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (fractions.isNotEmpty()) FontWeight.Bold else null,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                fractions.sortedBy { it.ordinal }.forEach { fraction ->
                    val color = when (fraction) {
                        WasteFraction.MIXED -> wasteColors.mixed
                        WasteFraction.YELLOW_BLUE -> wasteColors.metal
                        WasteFraction.GLASS -> wasteColors.glass
                        WasteFraction.BIO -> wasteColors.bio
                        WasteFraction.ASH -> wasteColors.mixed
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }
        }
    }
}

private fun YearMonth.labelPl(): String =
    "${month.getDisplayName(TextStyle.FULL_STANDALONE, polish).replaceFirstChar { it.uppercase() }} $year"

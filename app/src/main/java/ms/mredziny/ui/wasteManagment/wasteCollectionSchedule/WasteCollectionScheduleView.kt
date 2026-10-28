package ms.mredziny.ui.wasteManagment.wasteCollectionSchedule


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ms.mredziny.data.db.wastemanagement.Date
import ms.mredziny.data.db.wastemanagement.Place
import ms.mredziny.viewModel.WasteCollectionScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteCollectionScheduleScreen(navController: NavHostController, viewModel: WasteCollectionScheduleViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val currentState = uiState

    Column (
        modifier = Modifier
            .fillMaxWidth()
            //.padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    //.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "1. Wybierz miejscowość"
                )
                Spacer(modifier = Modifier.height(2.dp))
                DynamicSelectTextField(
                    selectedValue = when (currentState) {
                        is WasteCollectionScheduleUiState.PickedPlace -> currentState.pickedPlace.id to currentState.pickedPlace.name
                        is WasteCollectionScheduleUiState.PickedStreet -> currentState.pickedPlace.id to currentState.pickedPlace.name
                        is WasteCollectionScheduleUiState.PickedDate -> currentState.pickedPlace.id to currentState.pickedPlace.name
                        is WasteCollectionScheduleUiState.Success -> currentState.pickedPlace.id to currentState.pickedPlace.name
                        else -> 0 to ""
                    },
                    options = when (currentState) {
                        is WasteCollectionScheduleUiState.Idle -> currentState.placesList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.PickedPlace -> currentState.placesList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.PickedStreet -> currentState.placesList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.PickedDate -> currentState.placesList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.Success -> currentState.placesList.map { it.id to it.name }
                        else -> emptyList()
                    },
                    label = "Miejscowość",
                    onValueChangedEvent = { viewModel.setPlaceName(Place(it.first, it.second)) }
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    modifier = Modifier,
                    text = "2. Wybierz ulicę"
                )

                Spacer(modifier = Modifier.height(2.dp))

                DynamicSelectTextField(
                    selectedValue = when (currentState) {
                        is WasteCollectionScheduleUiState.PickedPlace -> currentState.pickedStreet.id to currentState.pickedStreet.name
                        is WasteCollectionScheduleUiState.PickedStreet -> currentState.pickedStreet.id to currentState.pickedStreet.name
                        is WasteCollectionScheduleUiState.PickedDate -> currentState.pickedStreet.id to currentState.pickedStreet.name
                        is WasteCollectionScheduleUiState.Success -> currentState.pickedStreet.id to currentState.pickedStreet.name
                        else -> 0 to ""
                    },
                    options = when (currentState) {
                        is WasteCollectionScheduleUiState.PickedPlace -> currentState.streetsList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.PickedStreet -> currentState.streetsList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.PickedDate -> currentState.streetsList.map { it.id to it.name }
                        is WasteCollectionScheduleUiState.Success -> currentState.streetsList.map { it.id to it.name }
                        else -> emptyList()
                    },
                    label = "Ulica",
                    onValueChangedEvent = {
                        viewModel.setStreetName(
                            FetchedStreet(
                                it.first,
                                it.second
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "3. Wybierz miesiąc")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState()),
                ) {
                    when(currentState){
                        is WasteCollectionScheduleUiState.Idle -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                        is WasteCollectionScheduleUiState.PickedPlace -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                        is WasteCollectionScheduleUiState.PickedStreet -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                        is WasteCollectionScheduleUiState.PickedDate -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                        is WasteCollectionScheduleUiState.Success -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                        is WasteCollectionScheduleUiState.Error -> {
                            currentState.dateList.forEach { date ->
                                DataPickerButton(viewModel, uiState, date)
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }
                }
            }
        }

                when (currentState) {
                    is WasteCollectionScheduleUiState.Success -> {
                        if(currentState.wasteCollectionSchedule == null)
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "Brak danych"
                                )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ){
                            val selectedIndexUiState = currentState.presentationType
                            val options = listOf("Lista", "Kalendarz")
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                options.forEachIndexed { index, label ->
                                    SegmentedButton(
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = options.size
                                        ),
                                        colors = SegmentedButtonDefaults.colors(
                                            activeContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        ),
                                        onClick = {
                                            viewModel.setPresentationType(index)
                                        },
                                        selected = index == selectedIndexUiState
                                    ) {
                                        Text(label)
                                    }
                                }
                            }

                        }
                        if (currentState.presentationType == 0)
                            WasteCollectionScheduleList(currentState.wasteCollectionSchedule!!)
                        else
                            WasteCollectionCalendar(currentState.wasteCollectionSchedule!!)
                    }
                    is WasteCollectionScheduleUiState.Error -> {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "Brak danych"
                        )
                    }
                    else -> {}
                }
        }
    }



@Composable
fun WasteCollectionScheduleList(wasteCollectionSchedule: FetchedWasteCollectionSchedule) {
    val wasteTypeName = mapOf(
        "0" to "Brak danych",
        "1" to "Worek zielony",
        "2" to "Worek żółty",
        "3" to "Worek niebieski",
        "4" to "Worek brązowy",
        "5" to "Odpady zmieszane",
        "6" to "Popiół"
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        wasteCollectionSchedule.wasteCollectionSchedule.forEach { (wasteType, days) ->
            // Use the wasteType to find the corresponding name
            val wasteTypeLabel = wasteTypeName[wasteType]

            HorizontalDivider(
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 4.dp
                )
            )
            Row {
                Text(
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    text = "$wasteTypeLabel: ${days.joinToString()}",
                )
            }
        }
    }
}


@Composable
fun WasteCollectionCalendar(wasteCollectionSchedule: FetchedWasteCollectionSchedule) {
    val monthAsInt = java.time.Month.valueOf(wasteCollectionSchedule.month).value
    val firstDayOfMonth = java.time.LocalDate.of(wasteCollectionSchedule.year, monthAsInt, 1)
    val firstDayOfWeek = ((firstDayOfMonth.dayOfWeek.value + 6) % 8) + 1
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val wasteCollectionDays = wasteCollectionSchedule.wasteCollectionSchedule.associate { it.first to it.second.toSet() }

    val wasteTypeColors = mapOf(
        "1" to Color(0xFF21810C),
        "2" to Color(0xFFFEB511),
        "3" to Color(0xFF19A0DD),
        "4" to Color(0xFF7E4017),
        "5" to Color(0xFF241F1F),
        "6" to Color.Gray
    )

    val daysOfWeekInPolish = listOf("Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Ndz")

    Column {
        Row {
            daysOfWeekInPolish.forEach { day ->
                Text(
                    modifier = Modifier
                        .width(40.dp)
                        .padding(1.dp),
                    textAlign = TextAlign.Center,
                    text = day
                )
            }
        }

        for (week in 0 until (daysInMonth + firstDayOfWeek + 6) / 7) {
            Row {
                for (day in 0..6) {
                    val date = week * 7 + day - firstDayOfWeek + 1
                    if (date in 1..daysInMonth) {
                        val wasteTypesForDay = wasteCollectionDays.entries.filter { date in it.value }.map { it.key }
                        val colorsForDay = wasteTypesForDay.mapNotNull { wasteTypeColors[it] }

                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                colorsForDay.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(color)
                                    )
                                }
                            }
                            Text(
                                text = date.toString(),
                                color = if (wasteTypesForDay.contains("5")) Color.White else Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun DataPickerButton(viewModel: WasteCollectionScheduleViewModel, uiState: WasteCollectionScheduleUiState, date: Date) {
    val isMonthYear = when (uiState) {
        is WasteCollectionScheduleUiState.Idle -> uiState.date.month == date.month && uiState.date.year == date.year
        is WasteCollectionScheduleUiState.PickedPlace -> uiState.date.month == date.month && uiState.date.year == date.year
        is WasteCollectionScheduleUiState.PickedStreet -> uiState.date.month == date.month && uiState.date.year == date.year
        is WasteCollectionScheduleUiState.PickedDate -> uiState.date.month == date.month && uiState.date.year == date.year
        is WasteCollectionScheduleUiState.Success -> uiState.date.month == date.month && uiState.date.year == date.year
        else -> false
    }
    val monthInPolish = monthMap[date.month]
    TextButton(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.large)
            .background(color = if (isMonthYear) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.background)
            .padding(1.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.medium
            ),
        onClick = { viewModel.setDate(Date(date.id, date.month, date.year)) }
    ) {
        Text(
            text = "$monthInPolish, " + date.year.toString(),
            color = if (isMonthYear) MaterialTheme.colorScheme.onTertiaryContainer else Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: Pair<Int,String>,
    options: List<Pair<Int,String>>,
    label: String,
    onValueChangedEvent: (Pair<Int,String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .padding(8.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue.second,
            onValueChange = { } ,
            label = {Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors().copy(
                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option: Pair<Int,String> ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = option.second) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                        focusManager.clearFocus()
                    },
                    colors = MenuItemColors(
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.tertiaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                )
            }
        }
    }
}

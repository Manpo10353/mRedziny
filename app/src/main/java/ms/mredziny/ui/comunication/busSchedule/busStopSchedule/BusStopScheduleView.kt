package ms.mredziny.ui.comunication.busSchedule.busStopSchedule

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ms.mredziny.data.db.communication.Schedule
import ms.mredziny.data.db.communication.TimeT
import ms.mredziny.viewModel.BusStopScheduleViewModel


@Composable
fun BusStopScheduleView(viewModel: BusStopScheduleViewModel, navController: NavHostController, busStopName: String, busStopId: Int, direction: String) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(busStopId,busStopName,direction) {
        viewModel.setDirectionAndBusStopName(busStopId,busStopName,direction)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    //.padding(8.dp)
                    .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = "Kierunek",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = direction,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = "Przystanek",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = busStopName,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min), // Ensures the Row adapts to the minimum height of its children
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                when (val currentState = uiState) {
                                    is BusStopScheduleUiState.Success -> {
                                        val locationString = "${currentState.location.latitude},${currentState.location.longitude}"
                                        navController.navigate("bus_line_map/$locationString")
                                    }
                                    else -> {
                                        // Handle other states or do nothing
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(top = 8.dp)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.shapes.extraLarge
                                )
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.shapes.extraLarge
                                ),
                        ) {
                            Text(
                                text = "Pokaż przystanek na mapie",
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(top = 8.dp)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.shapes.extraLarge
                                )
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.shapes.extraLarge
                                ),
                        ) {
                            Text(
                                text = "Pokaż rozkłady innych przewoźników",
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                            )
                        }
                    }

                }
                //Row(
                //    modifier = Modifier
                //        .fillMaxWidth(),
                //    horizontalArrangement = Arrangement.SpaceBetween
                //) {
                //    BusType(viewModel,uiState,1)//"GZK"
                //    BusType(viewModel,uiState,2)// "PKS"
                //    BusType(viewModel,uiState,3)//"PKC"
                //}
            }
            DayTypeBox(viewModel, uiState)
            //HorizontalDivider(
            //    modifier = Modifier
            //        .padding(8.dp)
            //        .fillMaxWidth()
            //)

            when (val currentState = uiState) {
                is BusStopScheduleUiState.Loading -> {
                    LoadingScreen()
                }

                is BusStopScheduleUiState.Success -> {
                    MyLazyVerticalGrid(currentState.busStopSchedule)
                }

                is BusStopScheduleUiState.Error -> {
                    ErrorScreen()
                }

                else -> {
                    //IdleScreen()
                }
            }
        }
    }
}

@Composable
fun ErrorScreen() {
    Text(text = "Brak danych")
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun DayTypeBox(viewModel: BusStopScheduleViewModel, uiState: BusStopScheduleUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensures Row adapts to the minimum height of its children
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            DayTypeButton(viewModel = viewModel, uiState = uiState, dayType = 1)
            DayTypeButton(viewModel = viewModel, uiState = uiState, dayType = 2)
            DayTypeButton(viewModel = viewModel, uiState = uiState, dayType = 3)
            DayTypeButton(viewModel = viewModel, uiState = uiState, dayType = 4)
        }
    }
}

@Composable
fun DayTypeButton(viewModel: BusStopScheduleViewModel, uiState: BusStopScheduleUiState, dayType: Int, modifier: Modifier = Modifier) {
    val isCurrentDayType = when (uiState) {
        is BusStopScheduleUiState.Success -> uiState.dayType == dayType
        is BusStopScheduleUiState.Error -> uiState.dayType == dayType
        is BusStopScheduleUiState.Loading -> uiState.dayType == dayType
        is BusStopScheduleUiState.Idle -> uiState.dayType == dayType
    }

    TextButton(
        onClick = { viewModel.setDayType(dayType) },
        modifier = modifier
            .fillMaxHeight() // Ensures the button fills the maximum height of the Row
            .padding(2.dp)
            .border(
                2.dp,
                if (isCurrentDayType) MaterialTheme.colorScheme.secondary else Color.Gray,
                MaterialTheme.shapes.medium
            )
            .background(
                if (isCurrentDayType) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                MaterialTheme.shapes.medium
            )
    ) {
        Text(
            text = when (dayType) {
                1 -> "Roboczy\nszkolny"
                2 -> "Soboty"
                3 -> "Niedziele\ni święta"
                4 -> "Roboczy\nwakacyjny"
                else -> "Error"
            },
            style = TextStyle(
                fontSize = 15.sp,
                color = if (isCurrentDayType) MaterialTheme.colorScheme.onSecondaryContainer else Color.Gray,
                textAlign = TextAlign.Center
            ),
            textAlign = TextAlign.Center // Ensures text is centered within the button
        )
    }
}


@Composable
fun MyLazyVerticalGrid(schedule: Schedule) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        schedule.times.forEach { (hour, pairs) ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TimeColumn(
                        hour,
                        modifier = Modifier
                            .weight(1f) // 1/8 szerokości ekranu
                            .border(1.dp, Color.Gray)

                    )
                    PairColumn(
                        pairs,
                        modifier = Modifier
                            .weight(7f) // 7/8 szerokości ekranu
                            .border(1.dp, Color.Gray)

                    )
                }
            }
        }
    }
}

@Composable
fun TimeColumn(hour: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(32.dp)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$hour",
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(4.dp)
        )
    }
}

@Composable
fun PairColumn(pairs: List<TimeT>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(32.dp)
            //.padding(vertical = 8.dp)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(" ")
            pairs.forEachIndexed { index, time ->
                Text(
                    text = "${time.minute.toString().padStart(2, '0')}${time.annotation}",
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier
                        .padding(
                            end = (if (index < pairs.size - 1) 8.dp else 0.dp),
                            bottom = 2.dp,
                            top = 2.dp,
                        )
                )
            }
        }
    }
}


@Composable
fun BusType(viewModel: BusStopScheduleViewModel, uiState: BusStopScheduleUiState, busLine: Int){ //moze busline type do zrobienia, ale na rzei niech bęzie na stringu
    val isCurrentBusLine = when (uiState) {
        is BusStopScheduleUiState.Success -> uiState.busLine == busLine
        is BusStopScheduleUiState.Error -> uiState.busLine == busLine
        is BusStopScheduleUiState.Loading -> uiState.busLine == busLine
        is BusStopScheduleUiState.Idle -> uiState.busLine == busLine
    }
    Log.d("BusStopScheduleViewModel", "isCurrentBusLine: $isCurrentBusLine")

    IconButton(
        onClick = { viewModel.setBusLine(busLine)  },
        modifier = Modifier
            .padding(2.dp)
            .wrapContentSize()
            .size(96.dp)
            .border(
                2.dp,
                if (isCurrentBusLine) MaterialTheme.colorScheme.secondary else Color.Gray,
                MaterialTheme.shapes.medium

            )

){
    Text(
        text = when (busLine) {
            1 -> "GZK"
            2 -> "PKS"
            3 -> "KPC"
            else -> "Error"
        },
        textAlign = TextAlign.Center,
        color = if (isCurrentBusLine) MaterialTheme.colorScheme.secondary else Color.Gray,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 32.sp),
    )

}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BusScheduleScreenPreview() {
    MyLazyVerticalGrid(Schedule(1, 1, mapOf(1 to listOf(TimeT(1, "Test")))))
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DayTypeButtonPreview() {
    //DayTypeButton("Soboty")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DayTypeBoxPreview() {
    //DayTypeBox()
}
package ms.mredziny.ui.comunication.busSchedule.busStops

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ms.mredziny.R
import ms.mredziny.data.db.Comunication.TransportType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusStopScreen(navController: NavHostController, viewModel: BusStopViewModel, direction: String) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(direction) {
        viewModel.setDirection(direction)
    }

    val currentState = uiState
    //if (currentState is BusStopUiState.Idle) {
    //    currentState.direction = direction
    //}
    Column (
        modifier = Modifier
            .fillMaxWidth()
            //.verticalScroll(rememberScrollState())
            .padding(
                top = 8.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = "Przystanki w kierunku \n$direction",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp),
                textAlign = TextAlign.Center,
            )
        }
            OutlinedTextField(
                value = when (currentState) {
                    is BusStopUiState.Idle -> currentState.searchQuery
                    is BusStopUiState.Loading -> currentState.searchQuery
                    is BusStopUiState.Success -> currentState.searchQuery
                    is BusStopUiState.Error -> currentState.searchQuery
                },
                onValueChange ={ viewModel.updateSearchQuery(it) },
                label = { Text(text = "Wyszukaj przystanek") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp,
                        start = 8.dp,
                        end = 8.dp),
                    //.clip(MaterialTheme.shapes.medium),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors().copy(
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
            )
        HorizontalDivider(modifier = Modifier.padding(8.dp))

        when (currentState) {
            is BusStopUiState.Loading -> {
                Text("Loading...")
                //Icon(imageVector = Icons.Rounded., contentDescription = )
            }
            is BusStopUiState.Success -> {
                LazyColumn {
                    items(currentState.filteredBusStops) { filteredBusStops ->
                        BusStopBox(fetchedBusStop = filteredBusStops, navController = navController,viewModel)
                        HorizontalDivider(modifier = Modifier.padding(8.dp))
                        Spacer(modifier = Modifier.size(2.dp))

                    }
                }
            }
            is BusStopUiState.Error -> {
                // Show error message
                Text("Error: ${currentState.message}")
            }
            else -> {
                // No-op
            }
        }
    }
}

@Composable
fun BusStopBox(fetchedBusStop: FetchedBusStop, navController: NavHostController, viewModel: BusStopViewModel){
    val uiState = viewModel.uiState.collectAsState()
    val direction = when (val currentState = uiState.value) {
        is BusStopUiState.Success -> currentState.direction
        else -> return
    }
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = Color.Transparent)
            .padding(8.dp)
            .clickable { navController.navigate("bus_stop_schedule/${fetchedBusStop.id}/${fetchedBusStop.busStopName}/$direction") }
    ){
        Column (
            modifier = Modifier
                .align(Alignment.CenterStart)
        ){
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Column {
                    Text(
                        text = fetchedBusStop.cityName,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )


                    Spacer(modifier = Modifier.padding(1.dp))
                    Text(
                        text = fetchedBusStop.busStopName,
                        textAlign = TextAlign.Start,
                    )
                }
                IconButton(onClick = { viewModel.toggleFavourite(fetchedBusStop)}) {
                    Icon(
                        imageVector = if (fetchedBusStop.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null)
                    //Log.d("fav", "${fetchedBusStop.favourite}")
                }
            }
            TransportTypesIcons(transportTypes = fetchedBusStop.transportTypes)
        }
    }
}

@Composable
fun TransportTypesIcons(transportTypes: List<TransportType>) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        //Icon(
        //    painter = painterResource(id = R.drawable.directions_bus_48px),
        //    contentDescription = "Bus",
        //    tint = if (transportTypes.contains(TransportType.BUS)) MaterialTheme.colorScheme.secondary else Color.Gray
        //)
        //Icon(
        //    painter = painterResource(id = R.drawable.train_48px),
        //    contentDescription = "Train",
        //    tint = if (transportTypes.contains(TransportType.TRAIN)) MaterialTheme.colorScheme.secondary else Color.Gray
        //)
        //Icon(
        //    painter = painterResource(id = R.drawable.directions_bike_48px),
        //    contentDescription = "Bike",
        //    tint = if (transportTypes.contains(TransportType.BIKE)) MaterialTheme.colorScheme.secondary else Color.Gray
        //)
        Box (
            modifier = Modifier
                .align(Alignment.CenterVertically),
        ){
            Text(
                text = "GZK",
                textAlign = TextAlign.Center,
                color = if (transportTypes.contains(TransportType.GZK)) MaterialTheme.colorScheme.secondary else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp)
            )
        }
        Box (
            modifier = Modifier
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = "MPK",
                textAlign = TextAlign.Center,
                color = if (transportTypes.contains(TransportType.MPK)) MaterialTheme.colorScheme.secondary else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp)
            )
        }
        Box (
            modifier = Modifier
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = "KPC",
                textAlign = TextAlign.Center,
                color = if (transportTypes.contains(TransportType.KPC)) MaterialTheme.colorScheme.secondary else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp)
            )
        }
        Box (
            modifier = Modifier
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = "PKS",
                textAlign = TextAlign.Center,
                color = if (transportTypes.contains(TransportType.PKS)) MaterialTheme.colorScheme.secondary else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp)
            )
        }
    }
}
@Preview
@Composable
fun BusStopBoxPreview(){
    //BusStopBox(fetchedBusStop = FetchedBusStop(1,"Strefa","Rędziny", "Rędziny",true, listOf(TransportType.BUS,TransportType.BIKE),0.0,0.0), navController = rememberNavController())
}@Preview
@Composable
fun BusStopScreenPreview(){
    //BusStopScreen(navController = rememberNavController(), viewModel = BusStopViewModel(), direction = "Częstochowa")
}

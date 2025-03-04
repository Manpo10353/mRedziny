package ms.mredziny.ui.comunication.busSchedule.busFavourite

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ms.mredziny.ui.comunication.busSchedule.busStops.FetchedBusStop
import ms.mredziny.ui.comunication.busSchedule.busStops.TransportTypesIcons

@Composable
fun BusFavouriteScreen(navController: NavHostController, viewModel: BusFavouriteViewModel, direction: String) {

    LaunchedEffect(direction) {
        viewModel.setDirection(direction)
    }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                //.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    //.padding(bottom = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            ) {
                //Column(
                //    modifier = Modifier
                //        .fillMaxWidth(),
                //    verticalArrangement = Arrangement.SpaceBetween,
                //    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kierunek $direction",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                //}
            }
            GoToSearchBox(navController, direction)
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            //NearestBusStopBox()
            //Spacer(modifier = Modifier.size(8.dp))
            //HorizontalDivider(modifier = Modifier.fillMaxWidth())
            FavouriteListBox(viewModel,navController)
        }
    }
@Composable
fun BusStopBox(fetchedBusStop: FetchedBusStop, navController: NavHostController, viewModel: BusFavouriteViewModel){
    val uiState = viewModel.uiState.collectAsState()
    val direction = when (val currentState = uiState.value) {
        is BusFavouriteUiState.Idle -> currentState.direction
        is BusFavouriteUiState.Loading -> currentState.direction
        is BusFavouriteUiState.Success -> currentState.direction
        is BusFavouriteUiState.Error -> currentState.direction
    }
    //Log.d("BusFavouriteScreen", "BusStopBox: $direction, ${fetchedBusStop.busStopName}, ${fetchedBusStop.id}")
    Box (
        modifier = Modifier
            .wrapContentWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = Color.Transparent)
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            .clickable { navController.navigate("bus_stop_schedule/${fetchedBusStop.id}/${fetchedBusStop.busStopName}/$direction") }
    ){
        Column (
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
        ){
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                IconButton(onClick = { viewModel.toggleFavourite(fetchedBusStop) }) {
                    Icon(imageVector = if (fetchedBusStop.favourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null)
                }
            }
            TransportTypesIcons(transportTypes = fetchedBusStop.transportTypes)
        }
    }
}
@Composable
fun NearestBusStop(fetchedBusStop: FetchedBusStop, navController: NavHostController){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
            .clickable { navController.navigate("bus_stop_schedule") }
    ){
        Column (
            modifier = Modifier
                .align(Alignment.CenterStart)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(
                    text = fetchedBusStop.cityName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(text = "900 m")
            }
            Spacer(modifier = Modifier.padding(1.dp))
            Text(
                text = fetchedBusStop.busStopName,
                textAlign = TextAlign.Start,
            )
            TransportTypesIcons(transportTypes = fetchedBusStop.transportTypes)
        }
    }
}

@Composable
fun NearestBusStopBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            Text(
                text = "Najbliższe przystanki",
                modifier = Modifier
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .padding(8.dp)

            ){
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Funkcja dostępna wkrótce"
                )
            }
            //NearestBusStop(FetchedBusStop(0, "Test1", "Test1", "Test1", false, emptyList(), 0.0, 0.0), rememberNavController())
           // Spacer(modifier = Modifier.size(2.dp))
            //NearestBusStop(FetchedBusStop(0, "Test2", "Test2", "Test2", false, emptyList(), 0.0, 0.0), rememberNavController())
        }
    }
}

@Composable
fun FavouriteListBox(viewModel: BusFavouriteViewModel, navController: NavHostController) {
    val uiState = viewModel.uiState.collectAsState()
    val currentState = uiState.value
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        Text(
            text = "Ulubione przystanki",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(8.dp)
        )

        when (currentState) {
            is BusFavouriteUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    currentState.favouriteBusStops.forEach { fetchedBusStop ->
                        //Log.d("BusFavouriteScreen", "FavouriteListBox: ${fetchedBusStop.id}")
                        BusStopBox(fetchedBusStop = fetchedBusStop, navController = navController,viewModel)
                        //HorizontalDivider(modifier = Modifier.padding(8.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
            is BusFavouriteUiState.Error -> {
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
fun GoToSearchBox(navController: NavHostController,direction: String){
    Box (
        modifier = Modifier
            .wrapContentSize()
            //.clip(MaterialTheme.shapes.medium)
            //.background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
           TextButton(
               modifier = Modifier
                   .wrapContentSize()
                   .padding(8.dp),
               colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
               ),
               onClick = { navController.navigate("bus_stops/$direction") },
           ) {
               Row() {
                   Text(
                       modifier = Modifier,
                       textAlign = TextAlign.Center,
                       text = "Przejdź do wyszukiwarki",
                       color = MaterialTheme.colorScheme.onSecondaryContainer
                   )
                   Spacer(modifier = Modifier.size(4.dp))
                   Icon(imageVector = Icons.Filled.Search, contentDescription = null)
               }
           }
       }
}

@Composable
@Preview
fun BusFavouriteScreenPreview() {
    //BusFavouriteScreen(navController = rememberNavController(), viewModel = BusFavouriteViewModel(), direction = "Rędziny")
}



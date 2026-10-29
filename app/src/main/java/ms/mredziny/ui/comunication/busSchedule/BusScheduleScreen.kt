package ms.mredziny.ui.comunication.busSchedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun BusSchedule(navController: NavHostController) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BusDirectionBox("Rędziny", Modifier.weight(1f), onClick = { navController.navigate("bus_favourite/Rędziny") })
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                BusDirectionBox("Częstochowa", Modifier.weight(1f), onClick = { navController.navigate("bus_favourite/Częstochowa") })
            }
        }

@Composable
fun BusDirectionBox(direction: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = "Kierunek",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(2.dp)
                    //.align(Alignment.Center)
            )
            Text(
                text = direction,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(2.dp)
                    //.align(Alignment.Center)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun BusSchedulePreview() {
    BusSchedule(navController = rememberNavController())
}
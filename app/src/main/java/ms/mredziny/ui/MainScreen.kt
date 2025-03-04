package ms.mredziny.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ms.mredziny.R
import ms.mredziny.Screen

@Composable
fun MainScreen(navController: NavController) {
    val showDialogNotWorkingYet = remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

            ){
                MainTile(navController, showDialogNotWorkingYet, communicationTilesList,MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer )
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController,showDialogNotWorkingYet, wasteManagementTilesList, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController,showDialogNotWorkingYet, mCommuneOfficeTilesList, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController,showDialogNotWorkingYet,tourismTilesList, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                NotWorkingYetDialog(showDialogNotWorkingYet)
            }
        }

@Composable
fun MainTile(navController: NavController, showDialogNotWorkingYet: MutableState<Boolean>, tileList: TileList,tintColor: Color,backgroundColor: Color) {
    val showAdditionalTiles = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = backgroundColor)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tileList.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Tile(
                    tileList.tiles[0],
                    tintColor,
                    showDialogNotWorkingYet,
                    Modifier.weight(1f),
                    navController,

                    )
                Spacer(modifier = Modifier.size(8.dp))
                Tile(
                    tileList.tiles[1],
                    tintColor,
                    showDialogNotWorkingYet,
                    Modifier.weight(1f),
                    navController,
                )
            }
            if(showAdditionalTiles.value){
                Spacer(modifier = Modifier.size(8.dp))
            }

                AnimatedVisibility(visible = showAdditionalTiles.value) {
                    Column {
                    for (i in 2 until tileList.tiles.size step 2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Tile(
                                tileList.tiles[i],
                                tintColor,
                                showDialogNotWorkingYet,
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                navController,

                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            if (i + 1 < tileList.tiles.size) {
                                Tile(
                                    tileList.tiles[i + 1],
                                    tintColor,
                                    showDialogNotWorkingYet,
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    navController,
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                if(tileList.tiles.size>2) {
                    ExpandTile(
                        Modifier.weight(1f),
                        showAdditionalTiles,
                        tintColor
                    )
                }
            }
        }
    }
}
@Composable
fun ExpandTile(modifier: Modifier = Modifier, showAdditionalTiles: MutableState<Boolean> , tintColor: Color) {
    TextButton(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth(),

        onClick = { showAdditionalTiles.value = !showAdditionalTiles.value })
    {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = if (showAdditionalTiles.value) R.drawable.keyboard_double_arrow_up_48px else R.drawable.keyboard_double_arrow_down_48px),
                contentDescription = "Rozwiń",
                tint = tintColor
            )
        }
    }
}

@Composable
fun Tile(tileData: TileData, tintColor: Color, showDialog: MutableState<Boolean>,modifier: Modifier = Modifier,navController: NavController? = null) {
  TextButton(
      modifier = modifier
          .clip(shape = MaterialTheme.shapes.large)
          .background(color = MaterialTheme.colorScheme.background)
          //.border(2.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large)
          .fillMaxWidth()
          .padding(1.dp),
      onClick = {
          if (tileData.route == null) {
              showDialog.value = true
          } else {
              navController?.navigate(tileData.route)
          }
      }) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id=tileData.icon),
                contentDescription = tileData.title,
                tint = tintColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = tileData.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = tintColor
            )
        }
    }
}

@Composable
fun NotWorkingYetDialog(
    showDialog: MutableState<Boolean>
) {
    if (showDialog.value) {
        AlertDialog(
            icon = {
                Icon(painterResource(id = R.drawable.engineering_48px), contentDescription = "Trwają prace")
            },
            text = {
                Text(
                    text = "Funkcja będzie dostępna wkrótce",
                    textAlign = TextAlign.Center,
                )
            },
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotWorkingYetDialogPreview() {
    val showDialog = remember { mutableStateOf(true) }
   // NotWorkingYetDialog(showDialog)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
   // MainScreen(navController = rememberNavController()) // Preview the MainScreen
}

@Preview
@Composable
fun MainTilePreview() {
       // MainTile(navController = rememberNavController(), showDialogNotWorkingYet = remember { mutableStateOf(false) }, communicationTilesList)
    }
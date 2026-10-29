package ms.mredziny.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import ms.mredziny.data.news.NewsItem
import ms.mredziny.viewModel.NewsViewModel
import androidx.core.net.toUri

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val showDialogNotWorkingYet = remember { mutableStateOf(false) }
    val newsViewModel: NewsViewModel = hiltViewModel()
    val latestNews by newsViewModel.latestNews.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

            ){
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    text = "Aktualności",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start
                )
                NewsCard(
                    news = latestNews,
                    onClick = {
                        latestNews?.originalUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        }
                    }
                )
                MainTile(navController, showDialogNotWorkingYet, communicationTilesList)
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController, showDialogNotWorkingYet, wasteManagementTilesList)
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController, showDialogNotWorkingYet, mCommuneOfficeTilesList)
                Spacer(modifier = Modifier.size(8.dp))
                MainTile(navController,showDialogNotWorkingYet,tourismTilesList)
                NotWorkingYetDialog(showDialogNotWorkingYet)
            }
        }

@Composable
fun MainTile(navController: NavController, showDialogNotWorkingYet: MutableState<Boolean>, tileList: TileList) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = tileList.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tileList.tiles.chunked(2).forEach { rowTiles ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTiles.forEach { tile ->

                            Tile(
                                tileData = tile,
                                showDialog = showDialogNotWorkingYet,
                                modifier = if (rowTiles.size == 1) {
                                    Modifier.fillMaxWidth()
                                } else {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Tile(tileData: TileData, showDialog: MutableState<Boolean>,modifier: Modifier = Modifier,navController: NavController? = null) {
    ElevatedButton(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = {
            if (tileData.route == null) {
                showDialog.value = true
            } else {
                navController?.navigate(tileData.route)
            }
        }) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
          ){
              Icon(
                  modifier = Modifier
                      .size(40.dp),
                  imageVector = tileData.icon,
                  contentDescription = tileData.title,
              )
              Spacer(modifier = Modifier.size(4.dp))
              Text(
                  text = tileData.title,
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.primary,
                  softWrap = true
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
                Icon(imageVector = Icons.Outlined.Engineering, contentDescription = "Trwają prace")
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

@Composable
fun NewsCard(
    news: NewsItem?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column {
            if (news != null) {
                if (news.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = news.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    maxLines = 2
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NotWorkingYetDialogPreview() {
    val showDialog = remember { mutableStateOf(true) }
    NotWorkingYetDialog(showDialog)
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
package ms.mredziny.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ms.mredziny.data.db.news.NewsEntity
import ms.mredziny.ui.theme.MredzinyTheme

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Start,
    )
}

@Composable
fun NewsCard(
    state: NewsUiState,
    onNewsClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is NewsUiState.Success -> NewsArticleCard(
            news = state.news,
            onClick = { onNewsClick(state.news.originalUrl) },
            modifier = modifier,
        )

        else -> Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    NewsUiState.Loading -> CircularProgressIndicator()
                    NewsUiState.Empty -> Text("Brak aktualności", color = MaterialTheme.colorScheme.onSurface)
                    NewsUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nie udało się pobrać aktualności", color = MaterialTheme.colorScheme.onSurface)
                        TextButton(onClick = onRetry) { Text("Spróbuj ponownie") }
                    }
                    is NewsUiState.Success -> Unit
                }
            }
        }
    }
}

@Composable
private fun NewsArticleCard(
    news: NewsEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column {
            if (news.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = news.imageUrl,
                    contentDescription = news.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp),
                maxLines = 2,
            )
        }
    }
}

@Composable
fun HomeSection(
    section: TileSection,
    onTileClick: (TileData) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        SectionTitle(section.title, modifier = Modifier.padding(0.dp))
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            section.tiles.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { tile ->
                        TileButton(
                            tile = tile,
                            onClick = { onTileClick(tile) },
                            modifier = if (row.size == 1) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TileButton(
    tile: TileData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 120.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = tile.title,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = tile.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun ComingSoonDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(imageVector = Icons.Outlined.Engineering, contentDescription = "Trwają prace")
        },
        text = {
            Text(text = "Funkcja będzie dostępna wkrótce", textAlign = TextAlign.Center)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeSectionPreview() {
    MredzinyTheme (true){
        HomeSection(
            section = homeSections.firstOrNull() ?: TileSection(
                title = "Komunikacja",
                tiles = listOf(
                    TileData("Rozkład jazdy", Icons.Outlined.Engineering),
                    TileData("Mapa linii", Icons.Outlined.Engineering),
                ),
            ),
            onTileClick = {},
        )
    }
}
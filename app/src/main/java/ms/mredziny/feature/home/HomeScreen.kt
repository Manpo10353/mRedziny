package ms.mredziny.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ms.mredziny.data.db.news.NewsEntity
import ms.mredziny.ui.theme.MredzinyTheme

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val newsState by viewModel.newsState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    HomeContent(
        newsState = newsState,
        onNewsClick = { url -> uriHandler.openUri(url) },
        onRetryNews = viewModel::refreshNews,
        onNavigate = onNavigate,
    )
}

@Composable
fun HomeContent(
    newsState: NewsUiState,
    onNewsClick: (String) -> Unit,
    onRetryNews: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    var showComingSoon by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTitle("Aktualności")
        NewsCard(
            state = newsState,
            onNewsClick = onNewsClick,
            onRetry = onRetryNews,
        )

        homeSections.forEach { section ->
            HomeSection(
                section = section,
                onTileClick = { tile ->
                    val route = tile.route
                    if (route == null) {
                        showComingSoon = true
                    } else {
                        onNavigate(route)
                    }
                },
            )
        }
    }

    if (showComingSoon) {
        ComingSoonDialog(onDismiss = { showComingSoon = false })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentSuccessPreview() {
    MredzinyTheme {
        HomeContent(
            newsState = NewsUiState.Success(
                NewsEntity(
                    originalUrl = "https://redziny.pl",
                    title = "Przykładowa aktualność",
                    imageUrl = "",
                    content = "",
                    date = null,
                )
            ),
            onNewsClick = {},
            onRetryNews = {},
            onNavigate = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    MredzinyTheme {
        HomeContent(
            newsState = NewsUiState.Error,
            onNewsClick = {},
            onRetryNews = {},
            onNavigate = {},
        )
    }
}
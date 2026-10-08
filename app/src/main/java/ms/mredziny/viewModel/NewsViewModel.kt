package ms.mredziny.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ms.mredziny.data.news.NewsDao
import ms.mredziny.data.news.NewsItem
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    application: Application,
    private val newsDao: NewsDao
) : AndroidViewModel(application) {

    val latestNews: StateFlow<NewsItem?> = newsDao.getLatestNews()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        refreshNews()
    }

    fun refreshNews() {
        viewModelScope.launch {
            val fetchedNews = fetchLatestNewsFromWeb()
            if (fetchedNews != null) {
                val current = latestNews.value
                if (current == null || current.originalUrl != fetchedNews.originalUrl || current.title != fetchedNews.title || current.imageUrl != fetchedNews.imageUrl) {
                    Log.d("NewsViewModel", "New/updated news article found: ${fetchedNews.title}")
                    newsDao.deleteAllNews()
                    newsDao.insertNews(fetchedNews)
                } else {
                    Log.d("NewsViewModel", "News is already up to date: ${fetchedNews.title}")
                }
            } else {
                Log.w("NewsViewModel", "Fetched news was null")
            }
        }
    }

    private suspend fun fetchLatestNewsFromWeb(): NewsItem? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://redziny.pl"
                Log.d("NewsViewModel", "Connecting to $url...")
                val document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get()

                // Primary extraction directly from .card-news (no URL filtering required)
                val firstCard = document.select(".card-news").firstOrNull()
                if (firstCard != null) {
                    val linkElement = firstCard.select("a[title]").firstOrNull()
                        ?: firstCard.select("a").firstOrNull()
                    val imageElement = firstCard.select("img").firstOrNull()

                    if (linkElement != null) {
                        val originalUrl = linkElement.attr("href")
                        val title = if (linkElement.hasAttr("title") && linkElement.attr("title").isNotBlank()) {
                            linkElement.attr("title")
                        } else {
                            linkElement.text()
                        }
                        val imageUrl = imageElement?.attr("src") ?: ""

                        if (title.isNotBlank() && originalUrl.isNotBlank()) {
                            Log.d("NewsViewModel", "Extracted from .card-news: title='$title', url='$originalUrl', img='$imageUrl'")
                            return@withContext NewsItem(
                                originalUrl = originalUrl,
                                title = title,
                                imageUrl = imageUrl,
                                content = "Kliknij, aby przeczytać pełny artykuł na stronie źródłowej.",
                                date = null
                            )
                        }
                    }
                }

                // Fallback in case .card-news is renamed in a future site update
                val fallbackLink = document.select("h2.entry-title a, h3.entry-title a, article a")
                    .firstOrNull { link ->
                        val href = link.attr("href")
                        href.startsWith("https://redziny.pl/") &&
                                !href.contains("/wiadomosci/") &&
                                !href.contains("/kategoria/")
                    }

                if (fallbackLink != null) {
                    val originalUrl = fallbackLink.attr("href")
                    val title = if (fallbackLink.hasAttr("title") && fallbackLink.attr("title").isNotBlank()) {
                        fallbackLink.attr("title")
                    } else {
                        fallbackLink.text()
                    }
                    val imageUrl = fallbackLink.parents().select("img").firstOrNull()?.attr("src") ?: ""

                    Log.d("NewsViewModel", "Extracted from fallback: title='$title', url='$originalUrl', img='$imageUrl'")
                    return@withContext NewsItem(
                        originalUrl = originalUrl,
                        title = title,
                        imageUrl = imageUrl,
                        content = "Kliknij, aby przeczytać pełny artykuł na stronie źródłowej.",
                        date = null
                    )
                }

                Log.w("NewsViewModel", "No valid news link found on $url")
                null
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error fetching news from web", e)
                null
            }
        }
    }
}

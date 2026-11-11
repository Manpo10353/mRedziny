package ms.mredziny.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ms.mredziny.data.db.news.NewsDao
import ms.mredziny.data.db.news.NewsEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsDao: NewsDao,
) {
    fun latestNews(): Flow<NewsEntity?> = newsDao.getLatestNews()

    suspend fun refresh(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val fetched = fetchLatest() ?: return@runCatching
            val current = newsDao.getLatestNews().first()
            val changed = current == null ||
                current.originalUrl != fetched.originalUrl ||
                current.title != fetched.title ||
                current.imageUrl != fetched.imageUrl
            if (changed) {
                newsDao.deleteAllNews()
                newsDao.insertNews(fetched)
            }
        }
    }

    private fun fetchLatest(): NewsEntity? {
        val document = Jsoup.connect(SOURCE_URL)
            .userAgent(USER_AGENT)
            .timeout(10_000)
            .get()

        // Główne źródło: kafelek .card-news (odporne na zmiany tytułów i linków)
        val card = document.select(".card-news").firstOrNull()
        val cardLink = card?.select("a[title]")?.firstOrNull() ?: card?.select("a")?.firstOrNull()
        if (card != null && cardLink != null) {
            val url = cardLink.attr("href")
            val title = cardLink.titleText()
            if (title.isNotBlank() && url.isNotBlank()) {
                return newsItem(url, title, card.select("img").firstOrNull()?.attr("src").orEmpty())
            }
        }

        // Zapasowe źródło, gdy serwis zmieni nazwy klas
        val fallback = document.select("h2.entry-title a, h3.entry-title a, article a")
            .firstOrNull { link ->
                val href = link.attr("href")
                href.startsWith("https://redziny.pl/") &&
                    !href.contains("/wiadomosci/") &&
                    !href.contains("/kategoria/")
            } ?: return null

        return newsItem(
            url = fallback.attr("href"),
            title = fallback.titleText(),
            imageUrl = fallback.parents().select("img").firstOrNull()?.attr("src").orEmpty(),
        )
    }

    private fun Element.titleText(): String =
        attr("title").takeIf { it.isNotBlank() } ?: text()

    private fun newsItem(url: String, title: String, imageUrl: String) = NewsEntity(
        originalUrl = url,
        title = title,
        imageUrl = imageUrl,
        content = "Kliknij, aby przeczytać pełny artykuł na stronie źródłowej.",
        date = null,
    )

    private companion object {
        const val SOURCE_URL = "https://redziny.pl"
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}

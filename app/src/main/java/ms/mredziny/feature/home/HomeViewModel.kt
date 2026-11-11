package ms.mredziny.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ms.mredziny.data.repository.NewsRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val isError = MutableStateFlow(false)

    val newsState: StateFlow<NewsUiState> = combine(
        newsRepository.latestNews(),
        isRefreshing,
        isError,
    ) { news, refreshing, error ->
        when {
            news != null -> NewsUiState.Success(news)
            refreshing -> NewsUiState.Loading
            error -> NewsUiState.Error
            else -> NewsUiState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NewsUiState.Loading,
    )

    init {
        refreshNews()
    }

    fun refreshNews() {
        viewModelScope.launch {
            isRefreshing.value = true
            isError.value = false
            val success = newsRepository.refresh().isSuccess
            if (!success) {
                isError.value = true
            }
            isRefreshing.value = false
        }
    }
}

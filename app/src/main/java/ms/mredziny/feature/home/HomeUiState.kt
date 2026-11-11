package ms.mredziny.feature.home

import ms.mredziny.data.db.news.NewsEntity

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data object Empty : NewsUiState
    data object Error : NewsUiState
    data class Success(val news: NewsEntity) : NewsUiState
}

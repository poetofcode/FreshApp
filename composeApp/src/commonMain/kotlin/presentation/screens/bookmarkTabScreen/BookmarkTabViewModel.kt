package presentation.screens.bookmarkTabScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.BookmarkRepository
import domain.model.PostModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class BookmarkTabViewModel(val bookmarkRepository: BookmarkRepository)
    : BaseViewModel() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    val state = mutableStateOf(State())

    init {
        fetchBookmarks()
    }

    fun fetchBookmarks() {
//        viewModelScope.launch {
//            val posts = bookmarkRepository.getAll().toList()
//            state.value = state.value.copy(posts = posts)
//        }

        val posts = bookmarkRepository.getAll().toList()
        state.value = state.value.copy(posts = posts)
    }


}
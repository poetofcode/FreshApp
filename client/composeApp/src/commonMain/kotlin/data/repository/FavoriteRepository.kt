package data.repository

import data.utils.AppDataStorage
import domain.model.PostModel
import domain.model.toFavoritePost
import domain.model.toPostModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface FavoriteRepository {

    val changesFlow : SharedFlow<ChangeInfo>

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

    suspend fun filterFavoriteIds(ids: List<String>) : List<String>

    suspend fun fetch(input: FetchFavoriteInput = FetchFavoriteInput()) : PagedResource<PostModel>

}

// TODO move to domain/model/
sealed interface ChangeInfo {
    data class AddedItem(val id: String) : ChangeInfo
    data class DeletedItem(val id: String) : ChangeInfo
}

// TODO move to domain/model/
data class FetchFavoriteInput(
    val query: String = "",
    val limit: Int = 30,
    val start: Int = 0,
    val sort: String = "",
)

// TODO move to domain/model/
data class PagedResource<T>(
    val isListCompleted: Boolean,
    val list: List<T>,
)

class FavoriteLocalRepositoryImpl(
    private val storage: AppDataStorage,
) : FavoriteRepository {

    private val mutableChangesFlow = MutableSharedFlow<ChangeInfo>(extraBufferCapacity = 1)

    override val changesFlow: SharedFlow<ChangeInfo>
        get() = mutableChangesFlow

    override suspend fun add(post: PostModel) {
        val favoritePosts = storage.fetchFavorites().filterNot {
            it.id == post.id
        }
        storage.saveFavorites(favoritePosts + listOf(post.toFavoritePost()))
        mutableChangesFlow.emit(ChangeInfo.AddedItem(post.id))
    }

    override suspend fun remove(id: String) {
        storage.saveFavorites(storage.fetchFavorites().filterNot {
            it.id == id
        })
        mutableChangesFlow.emit(ChangeInfo.DeletedItem(id))
    }

    override suspend fun filterFavoriteIds(ids: List<String>): List<String> {
        return storage.fetchFavorites()
            .filter { ids.contains(it.id) }
            .map { it.id }
    }

    override suspend fun fetch(input: FetchFavoriteInput) : PagedResource<PostModel> {
        val favoritePosts = storage.fetchFavorites().sortedByDescending {
            it.createdAt
        }.map { it.toPostModel() }
        return PagedResource(
            isListCompleted = true,
            list = favoritePosts,
        )
    }

}
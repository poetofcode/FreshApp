package data.repository

import domain.model.PostModel

interface BookmarkRepository {
    fun add(post: PostModel)
    fun remove(id: String)

    fun getAll(): Array<PostModel>
}
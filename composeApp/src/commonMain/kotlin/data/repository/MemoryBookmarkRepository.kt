package data.repository

import domain.model.PostModel

class MemoryBookmarkRepository : BookmarkRepository {

    private val posts: ArrayList<PostModel> = arrayListOf()

    override fun add(post: PostModel) {
        posts.add(post)
    }

    override fun remove(id: String) {
        posts.removeIf{
            it.id == id
        }
    }

    override fun getAll(): Array<PostModel> = posts.toTypedArray()

}
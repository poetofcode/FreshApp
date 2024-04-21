package data.repository

import domain.model.PostModel

class MemoryBookmarkRepository : BookmarkRepository {

    //private val posts: ArrayList<PostModel> = arrayListOf()
    private val posts: MutableList<PostModel> = mutableListOf()

    override fun add(post: PostModel) {
        posts.add(post)
    }

    override fun remove(id: String): List<PostModel> {
        posts.removeIf{
            it.id == id
        }
        return posts
    }

    override fun getAll(): List<PostModel> = posts

}
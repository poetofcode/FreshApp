package domain.model

data class FetchFavoriteInput(
    val query: String = "",
    val limit: Int = 30,
    val start: Int = 0,
    val sort: String = "",
)
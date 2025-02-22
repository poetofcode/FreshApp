package domain.model

data class PagedResource<T>(
    val isListCompleted: Boolean,
    val list: List<T>,
)
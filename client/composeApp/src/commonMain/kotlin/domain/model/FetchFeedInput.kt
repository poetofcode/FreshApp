package domain.model

import data.entity.FetchFeedRequestBody

data class FetchFeedInput(
    val sources: List<String> = emptyList(),
    val page: Int = 0,
    val lastTimestamp: Long? = null,
)

fun FetchFeedInput.toRequestBody() : FetchFeedRequestBody {
    return FetchFeedRequestBody(
        sources = sources,
        page = page,
        timestamp = lastTimestamp,
    )
}
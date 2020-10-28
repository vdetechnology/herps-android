package herbs.n.more.data.db.entities

data class DataComment(
    var total_ratings: Float? = null,
    var total_comment: Float? = null,
    var comments: List<Comment>? = null
)
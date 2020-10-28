package herbs.n.more.data.db.entities

data class Comment(
    var id: Int? = null,
    var author_name: String? = null,
    var author_email: String? = null,
    var author_url: String? = null,
    var author_avatar_urls: String? = null,
    var content: String? = null,
    var rating: Float? = null
)
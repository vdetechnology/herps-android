package herbs.n.more.data.db.entities

data class Product(
    /*var id: Int? = null,
    var product_name: String? = null,
    var image: String? = null,
    var is_like: Int? = null,
    var number_rate: Float? = null,
    var number_review: Int? = null,
    var price_sale: Double? = null,
    var price: Double? = null,
    var percent_sale: Float? = null*/
    val id: Int,
    val quote: String,
    val author: String,
    val thumbnail: String,
    val created_at: String?,
    val updated_at: String?
)
package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.Product

data class GetBestSellingResponse (
    val code: Int?,
    val success: Boolean?,
    val message: String,
    val data : List<Product>
)
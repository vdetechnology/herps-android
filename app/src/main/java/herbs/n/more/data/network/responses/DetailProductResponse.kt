package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.DetailProduct


data class DetailProductResponse (
    val code: Int?,
    val success: Boolean?,
    val message: String,
    val data : DetailProduct
)
package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.Product

data class GetBestSellingResponse (
    val isSuccessful: Boolean?,
    val quotes : List<Product>
)
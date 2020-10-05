package herbs.n.more.data.repositories

import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.data.network.responses.DetailProductResponse

class DetailProductRepository (
    private val api: MyApi
) : SafeApiRequest() {

    suspend fun getDetailProduct(id: String): DetailProductResponse {
        return apiRequest { api.getDetailProduct(id) }
    }

}
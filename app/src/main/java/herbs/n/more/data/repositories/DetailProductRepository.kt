package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.data.network.responses.DetailProductResponse
import herbs.n.more.data.network.responses.GetBestSellingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailProductRepository (
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getDetailProduct(id: String): DetailProductResponse {
        return apiRequest { api.getDetailProduct(id) }
    }

    suspend fun getPopulars(pageindex : Int):  GetBestSellingResponse{
        return apiRequest { api.getPopular(pageindex, 4) }
    }

    suspend fun getPopular(pageindex : Int): List<Product> {
        return withContext(Dispatchers.IO) {
            val response = apiRequest { api.getPopular(pageindex,4) }
            response.data
        }
    }

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)

    fun saveCart(cart: Cart) = db.getCartDao().saveCart(cart)

    fun getCountCart() = db.getCartDao().getCount()

    suspend fun getCartByID(id: Int) = db.getCartDao().getCartByID(id)
}
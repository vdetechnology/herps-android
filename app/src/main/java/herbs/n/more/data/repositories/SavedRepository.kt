package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest

class SavedRepository (
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getPopular(pageindex : Int): List<Product> {
        val response = apiRequest { api.getPopular(pageindex,8) }
        return response.data
    }

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)

    fun saveCart(cart: Cart) = db.getCartDao().saveCart(cart)

    fun getCountCart() = db.getCartDao().getCount()

    suspend fun getCartByID(id: Int) = db.getCartDao().getCartByID(id)
}
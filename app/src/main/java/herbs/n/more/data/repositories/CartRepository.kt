package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.data.network.responses.GetDiscountCodeResponse

class CartRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    fun getUser() = db.getUserDao().getuser()

    fun getAllCart() = db.getCartDao().getAllCart()

    fun getSumOrder() = db.getCartDao().getSumOrder()

    fun updateCart(cart: Cart) = db.getCartDao().updateCart(cart)

    fun deleteCart(cart: Cart) = db.getCartDao().deleteCart(cart)

    fun deleteAllCart() = db.getCartDao().deleteAllCart()

    suspend fun getDiscountCode(
        email: String
    ) : GetDiscountCodeResponse {
        return apiRequest{ api.getDiscountCode(email)}
    }
}
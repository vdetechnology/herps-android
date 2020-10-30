package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Comment
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.data.network.responses.AddCommentResponse
import herbs.n.more.data.network.responses.DetailProductResponse

class DetailProductRepository (
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getDetailProduct(id: String): DetailProductResponse {
        return apiRequest { api.getDetailProduct(id) }
    }

    suspend fun getRelatedProduct(productid : Int): List<Product> {
        val response = apiRequest { api.getRelatedProduct(productid,15) }
        return response.data
    }

    suspend fun getComments(productid : String, pageindex : Int, pagesize: Int): List<Comment>? {
        val response = apiRequest { api.getComments(productid, pageindex, pagesize) }
        return response.data.comments
    }

    suspend fun addComment(productid: String, userid: String, email: String, reviewer: String,
                           rating: Int, comment: String, agent: String): AddCommentResponse {
        return apiRequest { api.addComment(productid, userid, email, reviewer, rating, comment, agent) }
    }

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)

    fun saveCart(cart: Cart) = db.getCartDao().saveCart(cart)

    fun getCountCart() = db.getCartDao().getCount()

    suspend fun getCartByID(id: Int) = db.getCartDao().getCartByID(id)
}
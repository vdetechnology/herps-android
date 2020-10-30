package herbs.n.more.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Comment
import herbs.n.more.data.db.entities.DetailProduct
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.responses.AddCommentResponse
import herbs.n.more.data.repositories.DetailProductRepository
import herbs.n.more.util.ApiException
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import herbs.n.more.util.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class DetailProductViewModel (
    private val repository: DetailProductRepository
) : ViewModel() {
    var detailListener : DetailListener? = null
    private val detail = MutableLiveData<DetailProduct>()

    suspend fun getDetail(id: String): LiveData<DetailProduct> {
        return withContext(Dispatchers.Main) {
            getDetailProduct(id)
            detail
        }
    }

    private fun getDetailProduct(id: String) {
        detailListener?.onStarted()
        Coroutines.main {
            try {
                var authResponse = repository.getDetailProduct(id)
                authResponse.data?.let {
                    detailListener?.onSuccess()
                    detail.postValue(it)
                    return@main
                }
                detailListener?.onFailure(authResponse.message!!)
            }catch (e: ApiException){
                detailListener?.onFailure(Constant.API_ERROR)
            }catch (e: NoInternetException){
                detailListener?.onFailure(Constant.NO_INTERNET)
            } catch (e: Exception) {
                e.printStackTrace()
                detailListener?.onFailure(Constant.API_ERROR)
            }
        }
    }

    suspend fun getRelatedProduct(productid : Int): List<Product>? {
        try {
            return repository.getRelatedProduct(productid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun saveRecentlys(product: Product) {
        Coroutines.io {
            repository.saveProducts(product)
        }
    }

    fun saveCart(cart: Cart) {
        Coroutines.io {
            repository.saveCart(cart)
        }
    }

    val countCart = repository.getCountCart()

    suspend fun cartByID(id: Int): Cart{
        return repository.getCartByID(id)
    }

    suspend fun getComments(productid: String, pageindex : Int, pagesize: Int): List<Comment>? {
        try {
            return repository.getComments(productid, pageindex, pagesize)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun addComment(productid: String, userid: String, email: String,
                           reviewer: String, rating: Int, comment: String, agent: String): AddCommentResponse? {
        detailListener?.onStarted()
        try {
           return repository.addComment(productid, userid, email, reviewer, rating, comment, agent)

        } catch (e: ApiException) {
            detailListener?.onFailure(Constant.API_ERROR)
        } catch (e: NoInternetException) {
            detailListener?.onFailure(Constant.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            detailListener?.onFailure(Constant.API_ERROR)
        }
        return null
    }
}
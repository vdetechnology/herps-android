package herbs.n.more.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.DetailProduct
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.repositories.DetailProductRepository
import herbs.n.more.util.ApiException
import herbs.n.more.util.Coroutines
import herbs.n.more.util.NoInternetException

class DetailProductViewModel (
    private val repository: DetailProductRepository
) : ViewModel() {
    var detailListener : DetailListener? = null
    private val detail = MutableLiveData<DetailProduct>()
    private val populars = MutableLiveData<List<Product>>()

    fun getDetail(id: String): LiveData<DetailProduct> {
        getDetailProduct(id)
        return  detail

    }

    private fun getDetailProduct(id: String) {
        detailListener?.onStarted()
        Coroutines.main {
            try {
                var authResponse = repository.getDetailProduct(id)
                authResponse.data?.let {
                    detailListener?.onSuccess(it)
                    detail.postValue(it)
                    return@main
                }
                detailListener?.onFailure(authResponse.message!!)
            }catch (e: ApiException){
                detailListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                detailListener?.onFailure(e.message!!)
            }
        }
    }

    fun getPopulars(pageindex : Int): LiveData<List<Product>> {
        fetchPopulars(pageindex)
        return  populars

    }

    private fun fetchPopulars(pageindex : Int) {
        Coroutines.main {
            try {
                var authResponse = repository.getPopulars(pageindex)
                authResponse.data?.let {
                    populars.postValue(it)
                    return@main
                }
                detailListener?.onFailure(authResponse.message!!)
            }catch (e: ApiException){
                detailListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                detailListener?.onFailure(e.message!!)
            }
        }
    }

    suspend fun getPopular(pageindex : Int): LiveData<List<Product>> {
        return  repository.getPopular(pageindex)
    }
}
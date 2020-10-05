package herbs.n.more.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.DetailProduct
import herbs.n.more.data.repositories.DetailProductRepository
import herbs.n.more.util.ApiException
import herbs.n.more.util.Coroutines
import herbs.n.more.util.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailProductViewModel (
    private val repository: DetailProductRepository
) : ViewModel() {
    var detailListener : DetailListener? = null
    private val detail = MutableLiveData<DetailProduct>()

    fun getDetail(id: String): LiveData<DetailProduct> {
        getDetailProduct(id)
        return  detail

    }

    fun getDetailProduct(id: String) {
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
}
package herbs.n.more.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.data.network.responses.DetailProductResponse
import herbs.n.more.data.network.responses.GetBestSellingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class DetailProductRepository (
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    private val popular = MutableLiveData<List<Product>>()

    suspend fun getDetailProduct(id: String): DetailProductResponse {
        return apiRequest { api.getDetailProduct(id) }
    }

    suspend fun getPopulars(pageindex : Int):  GetBestSellingResponse{
        return apiRequest { api.getPopular(pageindex, 4) }
    }

    suspend fun getPopular(pageindex : Int): LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            fetchPopular(pageindex)
            popular
        }
    }

    private suspend fun fetchPopular(pageindex : Int) {
        try {
            val response = apiRequest { api.getPopular(pageindex,4) }
            popular.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)
}
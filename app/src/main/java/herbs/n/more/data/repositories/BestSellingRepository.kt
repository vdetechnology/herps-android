package herbs.n.more.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class BestSellingRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest(){

    private val bestSelling = MutableLiveData<List<Product>>()

    suspend fun getBestSelling(): LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            fetchProducts()
            bestSelling
        }
    }

    private suspend fun fetchProducts() {
        try {
            val response = apiRequest { api.getBestSelling() }
            bestSelling.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUser() = db.getUserDao().getuser()
}
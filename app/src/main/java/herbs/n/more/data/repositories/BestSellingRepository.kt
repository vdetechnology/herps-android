package herbs.n.more.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class BestSellingRepository(
    private val api: MyApi
) : SafeApiRequest(){

    private val bestSelling = MutableLiveData<List<Product>>()

    suspend fun getBestSelling(): LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            fetchQuotes()
            bestSelling
        }
    }

    private suspend fun fetchQuotes() {
        try {
            val response = apiRequest { api.getBestSelling() }
            bestSelling.postValue(response.quotes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
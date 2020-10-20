package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.SearchHistory
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest

class SearchRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    fun saveSearch(searchHistory: SearchHistory) = db.getSearchDao().saveHistory(searchHistory)

    fun getLimit3() = db.getSearchDao().getLimitThreeHistory()

    fun getLimit10() = db.getSearchDao().getLimitTenHistory()

    suspend fun deleteHistory() = db.getSearchDao().deleteAllHistory()

    fun getCount() = db.getSearchDao().getCount()

    suspend fun getHistory(title: String) = db.getSearchDao().getHistory(title)

    fun updateHistory(searchHistory: SearchHistory) = db.getSearchDao().updateHistory(searchHistory)
}
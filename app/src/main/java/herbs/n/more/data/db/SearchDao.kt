package herbs.n.more.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import herbs.n.more.data.db.entities.*


@Dao
interface SearchDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM SearchHistory ORDER BY update_date DESC LIMIT 3")
    fun getLimitThreeHistory() : LiveData<List<SearchHistory>>

    @Query("SELECT * FROM SearchHistory ORDER BY update_date DESC LIMIT 10")
    fun getLimitTenHistory() : LiveData<List<SearchHistory>>

    @Query("DELETE FROM SearchHistory")
    suspend fun deleteAllHistory()

    @Query("SELECT COUNT(title) FROM SearchHistory")
    fun getCount() : LiveData<Int>

    @Query("SELECT * FROM SearchHistory WHERE title = :title")
    suspend fun getHistory(title: String) : SearchHistory

    @Update
    fun updateHistory(searchHistory: SearchHistory)
}
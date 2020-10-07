package herbs.n.more.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import herbs.n.more.data.db.entities.Product

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProduct(product : Product)

    @Query("SELECT * FROM Product ORDER BY update_date DESC LIMIT 4")
    fun getProducts() : LiveData<List<Product>>

    @Query("SELECT * FROM Product ORDER BY update_date DESC")
    fun getAllProducts() : LiveData<List<Product>>
}
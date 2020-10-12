package herbs.n.more.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import herbs.n.more.data.db.entities.Cart


@Dao
interface CartDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCart(cart: Cart)

    @Query("SELECT * FROM Cart ORDER BY update_date DESC")
    fun getAllCart() : LiveData<List<Cart>>

    @Update
    fun updateCart(cart: Cart)

    @Delete
    fun deleteCart(cart: Cart)

    @Query("SELECT COUNT(id) FROM Cart")
    fun getCount() : LiveData<Int>

    @Query("SELECT * FROM Cart WHERE id = :idCart")
    suspend fun getCartByID(idCart: Int) : Cart

    @Query("SELECT SUM(total_order) FROM Cart")
    fun getSumOrder() : LiveData<Double>
}
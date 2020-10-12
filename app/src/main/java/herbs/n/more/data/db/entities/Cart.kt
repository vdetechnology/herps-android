package herbs.n.more.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var title: String? = null,
    var image: String? = null,
    var price: Double? = null,
    var sale_price: Double? = null,
    var total_sales: Int? = null,
    var total_sales_percent: String? = null,
    var amount: Int? = null,
    var update_date: Long? = null,
    var total_order: Double? = null
)
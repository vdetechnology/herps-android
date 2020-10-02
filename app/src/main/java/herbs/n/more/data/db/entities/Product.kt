package herbs.n.more.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var sku: String? = null,
    var status: String? = null,
    var title: String? = null,
    var image: String? = null,
    var price: String? = null,
    var sale_price: String? = null,
    var date_on_sale_from: String? = null,
    var date_on_sale_to: String? = null,
    var total_sales: Int? = null,
    var total_sales_percent: String? = null,
    var permalink: String? = null,
    var short_description: String? = null,
    var description: String? = null,
    var date: String? = null,
    var rating: Float? = null,
    var comment: Int? = null,
    var update_date: Long? = null
)
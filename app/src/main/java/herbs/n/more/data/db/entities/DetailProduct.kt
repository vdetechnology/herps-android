package herbs.n.more.data.db.entities

data class DetailProduct (
    var id: Int? = null,
    var sku: String? = null,
    var status: String? = null,
    var title: String? = null,
    var image: String? = null,
    var images: List<String>? = null,
    var price: String? = null,
    var sale_price: String? = null,
    var date_on_sale_from: String? = null,
    var date_on_sale_to: String? = null,
    var total_sales: Int? = null,
    var total_sales_percent: String? = null,
    var permalink: String? = null,
    var stock_status: String? = null,
    var short_description_html: String? = null,
    var short_description: String? = null,
    var description_html: String? = null,
    var description: String? = null,
    var rating: Int? = null,
    var comment: Int? = null
)
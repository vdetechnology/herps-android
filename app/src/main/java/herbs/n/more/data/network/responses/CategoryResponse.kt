package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.Category

data class CategoryResponse (
    val code: Int?,
    val success: Boolean?,
    val message: String,
    val data : List<Category>
)
package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.DataComment

data class GetCommentsResponse (
    val code: Int?,
    val success: Boolean?,
    val message: String,
    val data : DataComment
)
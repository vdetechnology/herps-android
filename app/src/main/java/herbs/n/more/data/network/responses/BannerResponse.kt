package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.SlideImage

data class BannerResponse (
    val code: Int?,
    val success: Boolean?,
    val message: String,
    val data : List<SlideImage>
)
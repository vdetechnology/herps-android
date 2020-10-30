package herbs.n.more.data.network.responses

data class PopularSearchesResponse(
    var code: Int,
    var success: Boolean,
    var message: String,
    var data : List<String>
)
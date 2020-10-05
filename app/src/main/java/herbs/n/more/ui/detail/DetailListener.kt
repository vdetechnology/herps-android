package herbs.n.more.ui.detail

import herbs.n.more.data.db.entities.DetailProduct

interface DetailListener {

    fun onStarted()
    fun onSuccess(detailProduct: DetailProduct)
    fun onFailure(message: String)
}
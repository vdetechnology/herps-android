package herbs.n.more.ui.cart

interface CartListener {

    fun onStarted()
    fun onSuccess(message: String)
    fun onSuccessCode(percent: Int)
    fun onFailure(message: String)
}
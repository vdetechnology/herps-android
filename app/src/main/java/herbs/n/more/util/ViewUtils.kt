package herbs.n.more.util

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.toast(messsage : String){
    Toast.makeText(this, messsage, Toast.LENGTH_SHORT).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.snackbar(messsage: String){
    Snackbar.make(this, messsage, Snackbar.LENGTH_SHORT).also {snackbar ->
        snackbar.setAction("OK"){
            snackbar.dismiss()
        }
    }.show()
}
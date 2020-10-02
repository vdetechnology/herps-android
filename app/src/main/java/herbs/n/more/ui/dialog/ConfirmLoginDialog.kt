package herbs.n.more.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import herbs.n.more.R
import herbs.n.more.ui.auth.AuthActivity


class ConfirmLoginDialog(private var mContext: Context) {


    fun show() {
        val factory = LayoutInflater.from(mContext)
        val view: View = factory.inflate(R.layout.dialog_confirm_login, null)
        val dialog: AlertDialog = AlertDialog.Builder(mContext).create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 64)
        dialog.window?.setBackgroundDrawable(inset)
        dialog.setView(view)
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val btnConfirm: Button = view.findViewById(R.id.btnAgree)
        val btnClose: Button = view.findViewById(R.id.btnNo)
        //txtMessage.text = message
        btnClose.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            val intent = Intent(mContext, AuthActivity::class.java)
            intent.putExtra("TYPE", 1)
            mContext?.startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }
}
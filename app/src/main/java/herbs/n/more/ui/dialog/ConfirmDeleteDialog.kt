package herbs.n.more.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.ui.cart.CartFragment


class ConfirmDeleteDialog(private var mContext: CartFragment) {

    private var onDialogClick: OnDialogClick? = null

    fun show(cart: Cart, editText: EditText) {
        onDialogClick = mContext
        val factory = LayoutInflater.from(mContext.requireContext())
        val view: View = factory.inflate(R.layout.dialog_confirm_delete, null)
        val dialog: AlertDialog = AlertDialog.Builder(mContext.requireContext()).create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 64)
        dialog.window?.setBackgroundDrawable(inset)
        dialog.setView(view)
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val btnConfirm: Button = view.findViewById(R.id.btnAgree)
        val btnClose: Button = view.findViewById(R.id.btnNo)
        //txtMessage.text = message
        btnClose.setOnClickListener {
            editText.setText(cart.amount.toString())
            editText.requestFocus()
            editText.setSelection(editText.text.length)
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            onDialogClick?.onOKClicked(cart)
            dialog.dismiss()
        }
        dialog.show()
    }

    interface OnDialogClick {
        fun onOKClicked(cart: Cart)
    }
}
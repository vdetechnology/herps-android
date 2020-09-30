package herbs.n.more.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import herbs.n.more.R
import herbs.n.more.ui.MainActivity


class RegisterSuccessDialogFragment(message: String) : BottomSheetDialogFragment() {

    private val message = message
    private var mListener: ItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.bottom_sheet_register, container, false)

        val btContinute: AppCompatButton = view.findViewById(R.id.bt_continute)
        val btBack: AppCompatButton = view.findViewById(R.id.bt_back_login)
        val tvStatus : TextView = view.findViewById(R.id.tv_status)
        val tvMessage : TextView = view.findViewById(R.id.tv_message)

        tvStatus.text = activity?.getString(R.string.login_error)
        tvMessage.text = message

        btContinute.setOnClickListener(View.OnClickListener {
            /*activity?.let {it ->
                Intent(it, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }*/
            mListener?.onContinuteClick()
        })

        btBack.setOnClickListener(View.OnClickListener {
            dismiss()
            activity?.onBackPressed()
        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as ItemClickListener
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogThemeNotCancel)
    }

    interface ItemClickListener {
        fun onContinuteClick()
    }

}
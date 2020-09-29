package herbs.n.more.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import herbs.n.more.R


class MessageDialogFragment(title: String, message: String) : BottomSheetDialogFragment() {

    private val title = title
    private val message = message

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.bottom_sheet_message, container, false)

        val btClose: AppCompatButton = view.findViewById(R.id.bt_close)
        val tvStatus : TextView = view.findViewById(R.id.tv_status)
        val tvMessage : TextView = view.findViewById(R.id.tv_message)

        tvStatus.text = title
        tvMessage.text = message

        btClose.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        return view
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
}
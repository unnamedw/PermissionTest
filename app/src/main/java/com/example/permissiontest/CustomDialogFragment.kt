package com.example.permissiontest

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.permissiontest.databinding.LayoutCustomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: LayoutCustomDialogBinding
    var listener: OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutCustomDialogBinding.inflate(inflater, container, false)
        binding.btnCancel.setOnClickListener {
            listener?.onCancel()
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            listener?.onConfirm()
            dismiss()
        }
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setTransparentBackground()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_200)))
        requireActivity().window?.apply {
//            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_200)))
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            statusBarColor = Color.RED
//            navigationBarColor = Color.RED // 배경색을 원하는 색으로 설정
        }
//        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)

        return binding.root.apply {
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    fun BottomSheetDialogFragment.setTransparentBackground() {
        dialog?.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.holo_red_dark)
            }
        }
    }

    interface OnClickListener {
        fun onConfirm()
        fun onCancel()
    }
}
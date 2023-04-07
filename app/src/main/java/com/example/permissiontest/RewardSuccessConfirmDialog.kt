package com.example.permissiontest

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.permissiontest.databinding.LayoutGetRewardSuccessDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RewardSuccessConfirmDialog: BottomSheetDialogFragment() {

    private lateinit var binding: LayoutGetRewardSuccessDialogBinding
    private lateinit var viewParam: ViewParam

    var onConfirm: ((BottomSheetDialogFragment) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            isCancelable = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewParam = (requireArguments().getSerializable(PARAM_VIEW_PARAM) as ViewParam?)
            ?: kotlin.run {
                dismiss()
                return
            }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutGetRewardSuccessDialogBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.tvTitle.text = viewParam.title
        binding.tvMessage.text = viewParam.message
        binding.tvReward.text = viewParam.reward
        binding.lavIcon.apply {
            maintainOriginalImageBounds = true
        }
        binding.cvConfirm.setOnClickListener {
            onConfirm?.invoke(this)
            dismiss()
        }
    }

    companion object {

        private const val PARAM_VIEW_PARAM = "PARAM_VIEW_PARAM"

        @JvmStatic
        fun newInstance(param: ViewParam): RewardSuccessConfirmDialog {
            val bundle = Bundle().apply {
                putSerializable(PARAM_VIEW_PARAM, param)
            }

            return RewardSuccessConfirmDialog().apply {
                arguments = bundle
            }
        }
    }

    data class ViewParam(
        val title: String,
        val message: String,
        val reward: String
    ): java.io.Serializable

}
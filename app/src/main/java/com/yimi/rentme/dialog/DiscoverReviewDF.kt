package com.yimi.rentme.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.DfDiscoverReviewBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment


class DiscoverReviewDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfDiscoverReviewBinding
    private var isMine = false
    private var content = ""
    private var isLike = false
    private var hint = ""
    private lateinit var discoverInfo: DiscoverInfo
    private lateinit var callBack: CallBack
    private val imm: InputMethodManager? by lazy { BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager? }

    override val layoutId: Int
        get() = R.layout.df_discover_review

    fun setIsMine(isMine: Boolean): DiscoverReviewDF {
        this.isMine = isMine
        return this
    }

    fun setContent(content: String): DiscoverReviewDF {
        this.content = content
        return this
    }

    fun setIsLike(isLike: Boolean): DiscoverReviewDF {
        this.isLike = isLike
        return this
    }

    fun setHint(hint: String): DiscoverReviewDF {
        this.hint = hint
        return this
    }

    fun setDiscoverInfo(discoverInfo: DiscoverInfo): DiscoverReviewDF {
        this.discoverInfo = discoverInfo
        return this
    }

    fun setCallBack(callBack: CallBack): DiscoverReviewDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfDiscoverReviewBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_DiscoverReviewDF")
    }

    override fun onStart() {
        super.onStart()
        cleanPadding()
    }

    override fun initUI() {
        binding.dialog = this
        binding.isMine = isMine
        binding.content = content
        binding.isLike = isLike
        binding.hint = hint
        binding.discoverInfo = discoverInfo
        // 发送
        binding.edContent.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dynDoReview()
            }
            true
        }
        binding.edContent.postDelayed({ showSoftInput() }, 200)
    }

    private fun showSoftInput() {
        imm?.let {
            binding.apply {
                binding.edContent.requestFocus()
                it.showSoftInput(binding.edContent, InputMethodManager.SHOW_FORCED)
            }
        }
    }

    private fun dynDoReview() {
        callBack.dynDoReview(binding.content!!)
        dismiss()
    }

    fun selectGift(view: View) {
        callBack.selectGift()
        dismiss()
    }

    fun dynLike(view: View) {
        callBack.dynLike()
        dismiss()
    }

    fun toReviewList(view: View) {
        callBack.toReviewList()
        dismiss()
    }

    override fun dismiss() {
        imm!!.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        imm!!.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
        super.onCancel(dialog)
    }

    interface CallBack {
        fun dynDoReview(content: String)
        fun selectGift()
        fun dynLike()
        fun toReviewList()
    }
}
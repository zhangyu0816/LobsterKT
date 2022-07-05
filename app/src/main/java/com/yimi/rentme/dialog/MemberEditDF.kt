package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.databinding.DfMemberEditBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment

class MemberEditDF(activity: AppCompatActivity) : BaseDialogFragment(activity, false, false) {

    private lateinit var binding: DfMemberEditBinding
    private var type = 0 // 3：个性签名
    private var hint = ""
    private var content = ""
    private var lines = 1
    private lateinit var callBack: CallBack

    override val layoutId: Int
        get() = R.layout.df_member_edit

    fun setType(type: Int): MemberEditDF {
        this.type = type
        return this
    }

    fun setHint(hint: String): MemberEditDF {
        this.hint = hint
        return this
    }

    fun setContent(content: String): MemberEditDF {
        this.content = content
        return this
    }

    fun setLines(lines: Int): MemberEditDF {
        this.lines = lines
        return this
    }

    fun setCallBack(callBack: CallBack): MemberEditDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfMemberEditBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_MemberEditDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.content = content
        binding.lines = lines
        binding.hint = hint
        binding.type = type
    }

    fun cleanContent(view: View) {
        binding.content = ""
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun sure(view: View) {
        callBack.sure(binding.content!!)
        dismiss()
    }

    interface CallBack {
        fun sure(content: String)
    }
}
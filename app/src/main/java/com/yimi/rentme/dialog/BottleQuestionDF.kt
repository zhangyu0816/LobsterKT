package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.databinding.DfBottleQuestionBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment

class BottleQuestionDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfBottleQuestionBinding

    override val layoutId: Int
        get() = R.layout.df_bottle_question

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfBottleQuestionBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_BottleQuestionDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.9)
    }

    override fun initUI() {
        binding.dialog = this
    }

    fun sure(view: View) {
        dismiss()
    }
}
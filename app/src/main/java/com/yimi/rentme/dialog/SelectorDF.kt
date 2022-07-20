package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.databinding.DfSelectorBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment

class SelectorDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfSelectorBinding
    lateinit var adapter: BaseAdapter<String>
    private var dataList = ArrayList<String>()
    private lateinit var callBack: CallBack

    override val layoutId: Int
        get() = R.layout.df_selector

    fun setDataList(dataList: ArrayList<String>): SelectorDF {
        this.dataList = dataList
        return this
    }

    fun setCallBack(callBack: CallBack): SelectorDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfSelectorBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_SelectorDF")
    }

    override fun initUI() {
        binding.dialog = this
        adapter = BaseAdapter(activity, R.layout.item_selector, dataList, this)
    }

    fun selectIndex(position: Int) {
        callBack.sure(position)
        dismiss()
    }

    fun cancel(view: View) {
        dismiss()
    }

    interface CallBack {
        fun sure(position: Int)
    }
}
package com.yimi.rentme.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.TabViewBinding

class TabView : RelativeLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView(context)
    }

    constructor(context: Context?) : super(context!!) {
        initView(context)
    }

    private lateinit var binding: TabViewBinding

    /**
     * 初始化view
     */
    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.tab_view, null, false
        ) as TabViewBinding
        binding.title = ""
        binding.tabSelect = false
        addView(binding.root)
    }

    fun selectTab(tabTitle: String, tabSelect: Boolean) {
        binding.title = tabTitle
        binding.tabSelect = tabSelect
    }

}
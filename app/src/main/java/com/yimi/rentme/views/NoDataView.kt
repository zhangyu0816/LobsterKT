package com.yimi.rentme.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.NoDataViewBinding

class NoDataView : RelativeLayout {
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

    private lateinit var binding: NoDataViewBinding

    /**
     * 初始化view
     */
    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.no_data_view, null, false
        ) as NoDataViewBinding
        binding.noDataRes = R.mipmap.empty_icon
        binding.noWifi = false
        addView(binding.root)
    }

    fun setNoDataInfo(noDataRes: Int, noWifi: Boolean) {
        binding.noDataRes = noDataRes
        binding.noWifi = noWifi
    }
}
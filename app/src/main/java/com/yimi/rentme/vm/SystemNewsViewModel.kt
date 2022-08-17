package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.SystemNews
import com.yimi.rentme.databinding.AcSystemNewsBinding

class SystemNewsViewModel : BaseViewModel() {

    lateinit var binding: AcSystemNewsBinding
    lateinit var adapter: BaseAdapter<SystemNews>
    private val systemNewsList = ArrayList<SystemNews>()
    private var pageNo = 1

    override fun initViewModel() {
        binding.title = "系统消息"
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }
}
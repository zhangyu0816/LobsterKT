package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.FragFollowBinding
import com.yimi.rentme.vm.BaseViewModel
import kotlinx.coroutines.Job

class FollowViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: FragFollowBinding
    lateinit var adapter: BaseAdapter<DiscoverInfo>
    private val discoverInfoList = ArrayList<DiscoverInfo>()
    private var pageNo = 1

    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_follow_discover, discoverInfoList, this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        discoverInfoList.clear()
        adapter.notifyDataSetChanged()
        attentionDyn()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        attentionDyn()
    }

    /**
     * 我关注的动态列表
     */
    private fun attentionDyn() {
        if (pageNo == 1)
            showLoading(Job(), "加载动态...")
        mainDataSource.enqueue({ attentionDyn(pageNo, 1) }) {
            onSuccess {
                binding.noData = false
                binding.noWifi = false
                dismissLoading()

            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noWifi = it.isNoWIFI
                if (it.isNoData) {
                    binding.noData = discoverInfoList.size == 0
                }
            }
        }
    }
}
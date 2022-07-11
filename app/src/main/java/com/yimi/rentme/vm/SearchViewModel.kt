package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcSearchBinding
import com.zb.baselibs.utils.SCToastUtil
import kotlinx.coroutines.Job

class SearchViewModel : BaseViewModel(), OnLoadMoreListener {

    lateinit var binding: AcSearchBinding
    lateinit var adapter: BaseAdapter<MemberInfo>
    private val memberInfoList = ArrayList<MemberInfo>()
    private var pageNo = 1

    @SuppressLint("NotifyDataSetChanged")
    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_search, memberInfoList, this)
        binding.refresh.setEnableRefresh(false)
        // 发送
        binding.edSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.edSearch.text.isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入虾菇号", 2)
                    return@setOnEditorActionListener true
                }
                pageNo = 1
                memberInfoList.clear()
                adapter.notifyDataSetChanged()
                hintKeyBoard2()
                search()
            }
            true
        }
        search()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        search()
    }

    /**
     * 搜索
     */
    private fun search() {
        if (pageNo == 1)
            showLoading(Job(), "等待搜索结果...")
        val map = HashMap<String, String>()
        map["pageNo"] = pageNo.toString()
        map["keyWord"] = binding.edSearch.text.toString()
        map["minAge"] = MineApp.minAge.toString()
        map["maxAge"] = MineApp.maxAge.toString()
        map["sex"] = if (MineApp.sex == 0) "1" else "0"
        mainDataSource.enqueue({ search(map) }) {
            onSuccess {
                dismissLoading()
                binding.noData = false
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishLoadMore()
                val start = memberInfoList.size
                memberInfoList.addAll(it)
                adapter.notifyItemRangeChanged(start, memberInfoList.size)
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishLoadMore()
                if (it.isNoData) {
                    binding.noData = memberInfoList.size == 0
                }
            }
        }
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(otherUserId: Long) {}
}
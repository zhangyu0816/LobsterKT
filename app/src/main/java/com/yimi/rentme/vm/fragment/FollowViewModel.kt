package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.FragFollowBinding
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.awesome.DownLoadUtil
import com.zb.baselibs.utils.getImageFile
import com.zb.baselibs.utils.getLong
import kotlinx.coroutines.Job
import org.jetbrains.anko.runOnUiThread

class FollowViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: FragFollowBinding
    lateinit var adapter: BaseAdapter<DiscoverInfo>
    private val discoverInfoList = ArrayList<DiscoverInfo>()
    private var pageNo = 1

    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_follow_discover, discoverInfoList, this)
        attentionDyn()
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
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        val url = item.videoUrl.ifEmpty {
                            if (item.images.isEmpty())
                                MineApp.followDaoManager.getImage(item.otherUserId)
                            else
                                item.images.split(",")[0]
                        }
                        if (url.contains(".mp4")) {
                            val start = discoverInfoList.size
                            discoverInfoList.add(item)
                            adapter.notifyItemRangeChanged(start, discoverInfoList.size)
                        } else {
                            DownLoadUtil.downLoad(
                                url, getImageFile(), object : DownLoadUtil.CallBack {
                                    override fun onFinish(filePath: String) {
                                        val start = discoverInfoList.size
                                        val bitmap = BitmapFactory.decodeFile(filePath)
                                        if (bitmap != null) {
                                            item.width = bitmap.width
                                            item.height = bitmap.height
                                        }
                                        discoverInfoList.add(item)
                                        if (MineApp.followDaoManager.getFollow(item.otherUserId)) {
                                            adapter.notifyItemRangeChanged(
                                                start, discoverInfoList.size
                                            )
                                        } else {
                                            activity.runOnUiThread {
                                                setImage(start, item.otherUserId)
                                            }

                                        }
                                        Log.e("downLoad", "${bitmap.width}____${bitmap.height}")
                                    }
                                })
                        }
                    }
                }
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
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

    /**
     * 保存关注信息
     */
    private fun setImage(start: Int, otherUserId: Long) {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.isFollow = true
                    followInfo.image = it.singleImage
                    followInfo.nick = it.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                    adapter.notifyItemRangeChanged(start, discoverInfoList.size)
                }
            }
        }
    }
}
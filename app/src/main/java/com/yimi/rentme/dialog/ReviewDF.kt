package com.yimi.rentme.dialog

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.ApiService
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.Review
import com.yimi.rentme.databinding.DfReviewBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import org.jetbrains.anko.startActivity

class ReviewDF(activity: AppCompatActivity) : BaseDialogFragment(activity), OnRefreshListener,
    OnLoadMoreListener {

    private lateinit var binding: DfReviewBinding
    private lateinit var mainDataSource: MainDataSource<ApiService>
    private var friendDynId = 0L // 动态ID
    private var otherUserId = 0L // 动态的主人
    private var reviews = 0 // 评论数
    private lateinit var callBack: CallBack
    private var pageNo = 1
    private var reviewId = 0L
    lateinit var adapter: BaseAdapter<Review>
    private val reviewList = ArrayList<Review>()

    override val layoutId: Int
        get() = R.layout.df_review

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): ReviewDF {
        this.mainDataSource = mainDataSource
        return this
    }

    fun setFriendDynId(friendDynId: Long): ReviewDF {
        this.friendDynId = friendDynId
        return this
    }

    fun setOtherUserId(otherUserId: Long): ReviewDF {
        this.otherUserId = otherUserId
        return this
    }

    fun setReviews(reviews: Int): ReviewDF {
        this.reviews = reviews
        return this
    }

    fun setCallBack(callBack: CallBack): ReviewDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfReviewBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_ReviewDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.reviews = reviews
        binding.name = ""
        binding.content = ""
        binding.noData = false

        adapter = BaseAdapter(activity, R.layout.item_df_review, reviewList, this)
        seeReviews()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        reviewList.clear()
        adapter.notifyDataSetChanged()
        seeReviews()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        seeReviews()
    }

    /**
     * 动态评论
     */
    private fun seeReviews() {
        mainDataSource.enqueue({ seeReviews(friendDynId, 1, pageNo, 10) }) {
            onSuccess {
                binding.noData = false
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                for (item in it) {
                    item.mainId = otherUserId
                }
                val start = reviewList.size
                reviewList.addAll(it)
                adapter.notifyItemRangeChanged(start, reviewList.size)
            }
            onFailed {
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData) {
                    binding.noData = reviewList.size == 0
                }
            }
        }
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(review: Review) {
        if (review.userId != getLong("userId"))
            activity.startActivity<MemberDetailActivity>(
                Pair("otherUserId", review.userId)
            )
    }

    /**
     * 选择评论的人
     */
    fun selectReview(review: Review) {
        reviewId = if (reviewId == review.reviewId) 0L else review.reviewId
        binding.name = if (reviewId == 0L) "" else review.nick
    }

    /**
     * 发送评论
     */
    fun sendReview(view: View) {
        if (binding.content!!.isEmpty()) {
            SCToastUtil.showToast(activity, "请输入评论内容", 2)
            return
        }
        val map = HashMap<String, String>()
        if (reviewId > 0)
            map["reviewId"] = reviewId.toString()
        map["friendDynId"] = friendDynId.toString()
        map["text"] = binding.content!!
        mainDataSource.enqueueLoading({ dynDoReview(map) }, "提交评论...") {
            onSuccess {
                SCToastUtil.showToast(activity, "发布成功", 2)
                binding.content = ""
                reviews += 1
                binding.reviews = reviews
                onRefresh(binding.refresh)
                callBack.sure()
            }
        }
    }

    fun close(view: View) {
        dismiss()
    }

    interface CallBack {
        fun sure()
    }
}
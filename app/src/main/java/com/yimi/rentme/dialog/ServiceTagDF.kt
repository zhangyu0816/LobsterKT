package com.yimi.rentme.dialog

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.databinding.DfServiceTagBinding
import com.yimi.rentme.roomdata.TagDaoManager
import com.yimi.rentme.roomdata.TagInfo
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.SimulateNetAPI
import org.json.JSONArray

class ServiceTagDF(activity: AppCompatActivity) : BaseDialogFragment(activity, false, false) {

    private lateinit var binding: DfServiceTagBinding
    private var serviceTags = ""
    private lateinit var callBack: CallBack
    lateinit var selectAdapter: BaseAdapter<String>
    val selectList = ArrayList<String>() // 选中的标签
    lateinit var tabAdapter: BaseAdapter<String>
    private val tabList = ArrayList<String>() // 标签标题
    private var mTabPosition = 0
    lateinit var tagAdapter: BaseAdapter<String>
    private val tagList = ArrayList<String>() // 所有标签
    private lateinit var tagDaoManager: TagDaoManager

    override val layoutId: Int
        get() = R.layout.df_service_tag

    fun setServiceTags(serviceTags: String): ServiceTagDF {
        this.serviceTags = serviceTags
        if (serviceTags.isNotEmpty()) {
            val tags = serviceTags.substring(1, serviceTags.length - 1).split("#")
            for (tag in tags)
                selectList.add(tag)
        }
        return this
    }

    fun setCallBack(callBack: CallBack): ServiceTagDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfServiceTagBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_ServiceTagDF")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initUI() {
        tabList.add("性格标签")
        tabList.add("我的爱好")
        tabList.add("玩什么")
        tabList.add("看什么")

        binding.dialog = this
        binding.showTag = selectList.size > 0
        selectAdapter = BaseAdapter(activity, R.layout.item_service_tag, selectList, this)
        tabAdapter = BaseAdapter(activity, R.layout.item_tab, tabList, this)
        tabAdapter.setSelectIndex(mTabPosition)
        tagAdapter = BaseAdapter(activity, R.layout.item_tag_list, tagList, this)

        tagDaoManager = TagDaoManager(BaseApp.context)
        BaseApp.fixedThreadPool.execute {
            tagList.addAll(tagDaoManager.getTagList(tabList[0]) as ArrayList<String>)
            if (tagList.size == 0) {
                val data = SimulateNetAPI.getOriginalFundData("tag.json")
                val array = JSONArray(data)
                for (i in 0 until array.length()) {
                    val jobObject = array.optJSONObject(i)
                    val tags = jobObject.optString("tags").split(",")
                    for (tag in tags) {
                        val tagInfo = TagInfo()
                        tagInfo.tagName = jobObject.optString("name")
                        tagInfo.tag = tag
                        tagDaoManager.insert(tagInfo)
                    }
                }
                tagList.addAll(tagDaoManager.getTagList(tabList[0]) as ArrayList<String>)
            }
            activity.runOnUiThread {
                tagAdapter.notifyItemRangeChanged(0, tagList.size)
            }
        }
    }

    /**
     * 删除选中的标签
     */
    fun deleteTag(position: Int) {
        selectList.removeAt(position)
        selectAdapter.notifyItemRemoved(position)
        selectAdapter.notifyItemRangeChanged(position, selectList.size - position)
        tagAdapter.notifyItemRangeChanged(0, tagList.size)
        binding.showTag = selectList.size > 0
    }

    /**
     * 标签标题
     */
    @SuppressLint("NotifyDataSetChanged")
    fun selectTab(position: Int) {
        tabAdapter.setSelectIndex(position)
        tabAdapter.notifyItemChanged(position)
        tabAdapter.notifyItemChanged(mTabPosition)
        mTabPosition = position
        tagList.clear()
        tagAdapter.notifyDataSetChanged()
        BaseApp.fixedThreadPool.execute {
            tagList.addAll(tagDaoManager.getTagList(tabList[position]) as ArrayList<String>)
            activity.runOnUiThread {
                tagAdapter.notifyItemRangeChanged(0, tagList.size)
            }
        }
    }

    /**
     * 选择标签
     */
    fun selectTag(position: Int) {
        if (selectList.contains(tagList[position])) {
            selectList.remove(tagList[position])
        } else {
            if (selectList.size >= 6) {
                SCToastUtil.showToast(activity, "最多发布6个标签哦", 2)
                return
            }
            selectList.add(tagList[position])
        }
        selectAdapter.notifyItemRangeChanged(0, selectList.size)
        tagAdapter.notifyItemChanged(position)
        binding.showTag = selectList.size > 0
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun sure(view: View) {
        if (selectList.size == 0)
            callBack.sure("")
        else {
            var tags = "#"
            for (tag in selectList) {
                tags += "${tag}#"
            }
            callBack.sure(tags)
        }
        dismiss()
    }

    interface CallBack {
        fun sure(serviceTags: String)
    }
}
package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MainActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.RegisterInfo
import com.yimi.rentme.databinding.FragRegisterMemberBinding
import com.yimi.rentme.dialog.JobDF
import com.yimi.rentme.dialog.MemberEditDF
import com.yimi.rentme.dialog.ServiceTagDF
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.bean.ThreeInfo
import com.zb.baselibs.utils.*
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class RegisterMemberViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterMemberBinding
    lateinit var adapter: BaseAdapter<String>
    private val tagList = ArrayList<String>()

    override fun initViewModel() {
        tagList.add("旅行")
        tagList.add("摄影")
        tagList.add("乐观主义")
        tagList.add("老实孩子")
        tagList.add("简单")
        tagList.add("音乐会")

        MineApp.registerInfo.serviceTags = "#"
        for (tag in tagList) {
            MineApp.registerInfo.serviceTags += "$tag#"
        }
        binding.job = ""
        binding.personalitySign = ""
        adapter = BaseAdapter(activity, R.layout.item_tag, tagList, this)
    }

    /**
     * 选择工作
     */
    fun selectJob(view: View) {
        JobDF(activity).setJobTitle(MineApp.registerInfo.jobTitle)
            .setJobName(MineApp.registerInfo.job).setCallBack(object : JobDF.CallBack {
                override fun sure(jobTitle: String, jobName: String) {
                    MineApp.registerInfo.jobTitle = jobTitle
                    MineApp.registerInfo.job = jobName
                    binding.job = jobName
                }

            }).show(activity.supportFragmentManager)
    }

    /**
     * 填写个性签名
     */
    fun editSign(view: View) {
        MemberEditDF(activity).setType(3).setHint("编辑个性签名...")
            .setContent(MineApp.registerInfo.personalitySign)
            .setLines(10).setCallBack(object : MemberEditDF.CallBack {
                override fun sure(content: String) {
                    MineApp.registerInfo.personalitySign = content
                    binding.personalitySign = content
                }
            }).show(activity.supportFragmentManager)
    }

    /**
     * 选择个性标签
     */
    fun selectTag(view: View) {
        ServiceTagDF(activity).setServiceTags(MineApp.registerInfo.serviceTags)
            .setCallBack(object : ServiceTagDF.CallBack {
                @SuppressLint("NotifyDataSetChanged")
                override fun sure(serviceTags: String) {
                    MineApp.registerInfo.serviceTags = serviceTags
                    tagList.clear()
                    adapter.notifyDataSetChanged()
                    if (serviceTags.isNotEmpty()) {
                        val tags = serviceTags.substring(1, serviceTags.length - 1).split("#")
                        for (tag in tags)
                            tagList.add(tag)
                        adapter.notifyItemRangeChanged(0, tagList.size)
                    }

                }
            }).show(activity.supportFragmentManager)
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (MineApp.threeInfo.openId.isEmpty())
            register()
        else
            loginByUnion(MineApp.threeInfo)
    }

    /**
     * 注册
     */
    private fun register() {
        val map = HashMap<String, String>()
        map["userName"] = MineApp.registerInfo.phone
        map["captcha"] = MineApp.registerInfo.captcha
        map["moreImages"] = MineApp.registerInfo.moreImages
        map["nick"] = MineApp.registerInfo.name
        map["sex"] = MineApp.registerInfo.sex.toString()
        map["birthday"] = MineApp.registerInfo.birthday
        map["provinceId"] = "0"
        map["cityId"] = "0"
        map["districtId"] = "0"
        map["device"] = "Android"
        map["deviceSysVersion"] = Build.VERSION.RELEASE
        map["deviceCode"] = getString("deviceCode")
        map["channelId"] = getString("channelId")
        map["usePl"] = "2"
        map["appVersion"] = activity.versionName()
        map["deviceHardwareInfo"] = getString("deviceHardwareInfo")
        showLoading(Job(), "注册并登录...")
        mainDataSource.enqueue({ register(map) }) {
            onSuccess {
                saveLong("userId", it.id)
                saveString("sessionId", it.sessionId)
                saveInteger("myIsThreeLogin", 0)
                saveString("loginName", it.userName)
                saveString("loginPass", "")
                modifyMemberInfo()
            }
            onFailed { dismissLoading() }
        }
    }

    /**
     * 第三方登录
     */
    private fun loginByUnion(threeInfo: ThreeInfo) {
        val map = HashMap<String, String>()
        map["openId"] = threeInfo.openId
        map["unionId"] = threeInfo.unionId
        map["unionNick"] = threeInfo.unionNick
        map["unionImage"] = threeInfo.unionImage
        map["unionSex"] = threeInfo.unionSex.toString()
        map["unionType"] = threeInfo.unionType.toString()
        map["device"] = "android"
        map["deviceSysVersion"] = Build.VERSION.RELEASE
        map["deviceCode"] = getString("deviceCode")
        map["channelId"] = getString("channelId")
        map["usePl"] = "2"
        map["appVersion"] = activity.versionName()
        map["deviceHardwareInfo"] = getString("deviceHardwareInfo")

        map["userName"] = MineApp.registerInfo.bindPhone
        map["captcha"] = MineApp.registerInfo.captcha
        map["moreImages"] = MineApp.registerInfo.moreImages
        map["nick"] = MineApp.registerInfo.name
        map["sex"] = MineApp.registerInfo.sex.toString()
        map["birthday"] = MineApp.registerInfo.birthday
        map["provinceId"] = "0"
        map["cityId"] = "0"
        map["districtId"] = "0"
        mainDataSource.enqueue({ loginByUnion(map) }) {
            onSuccess {
                saveLong("userId", it.id)
                saveString("sessionId", it.sessionId)
                saveInteger("myIsThreeLogin", 1)
                saveString("loginName", "")
                saveString("loginPass", "")
                modifyMemberInfo()
            }

            onFailed {
                dismissLoading()
            }
        }
    }

    /**
     * 更新用户信息
     */
    private fun modifyMemberInfo() {
        val map = HashMap<String, String>()
        map["nick"] = MineApp.registerInfo.name
        map["image"] = MineApp.registerInfo.image
        map["moreImages"] = MineApp.registerInfo.moreImages
        map["personalitySign"] = MineApp.registerInfo.personalitySign.ifEmpty { "有趣之人终相遇" }
        map["serviceTags"] = MineApp.registerInfo.serviceTags
        map["birthday"] = MineApp.registerInfo.birthday
        map["age"] = DateUtil.getAge(MineApp.registerInfo.birthday, 31).toString()
        map["sex"] = MineApp.registerInfo.sex.toString()
        map["constellation"] = "0"
        map["job"] = MineApp.registerInfo.job.ifEmpty { "设计师" }
        map["provinceId"] = "0"
        map["cityId"] = "0"
        map["districtId"] = "0"
        map["height"] = "0"
        map["singleImage"] = ""
        mainDataSource.enqueue(
            { modifyMemberInfo(map) })
        {
            onSuccess {
                myInfo()
            }
            onFailed { dismissLoading() }
        }
    }

    /**
     * 获取用户信息
     */
    private fun myInfo() {mainDataSource.enqueue({ myInfo()
         }) {
            onSuccess {
                dismissLoading()
                MineApp.mineInfo = it
                MineApp.sex = it.sex
                MineApp.registerInfo = RegisterInfo()
                activity.startActivity<MainActivity>()
                EventBus.getDefault().post("", "lobsterFinishLogin")
            }
            onFailed {
                dismissLoading()
            }
        }
    }
}
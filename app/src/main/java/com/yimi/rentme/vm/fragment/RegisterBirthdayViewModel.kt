package com.yimi.rentme.vm.fragment

import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.databinding.FragRegisterBirthdayBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BirthdayDF
import com.zb.baselibs.utils.DateUtil
import org.simple.eventbus.EventBus

class RegisterBirthdayViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterBirthdayBinding

    override fun initViewModel() {
        binding.canNext = false
        binding.birthday = ""
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            activity.runOnUiThread {
                selectBirthday(binding.tvBirthday)
            }
        }
    }

    /**
     * 选择生日
     */
    fun selectBirthday(view: View) {
        val year = DateUtil.getNow(DateUtil.yyyy).toInt()
        BirthdayDF(activity).setBirthday(binding.birthday!!).setMinYear(1960).setMaxYear(year - 18)
            .setCallBack(object : BirthdayDF.CallBack {
                override fun sure(birthday: String) {
                    binding.birthday = birthday
                    MineApp.registerInfo.birthday = birthday
                    binding.canNext = true
                }
            }).show(activity.supportFragmentManager)
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (binding.canNext) {
            EventBus.getDefault().post("","lobsterRegisterImage")
        }
    }
}
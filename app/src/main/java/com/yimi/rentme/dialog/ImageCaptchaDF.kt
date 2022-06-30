package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.R
import com.yimi.rentme.bean.ImageCaptcha
import com.yimi.rentme.databinding.DfImageCaptchaBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource

class ImageCaptchaDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfImageCaptchaBinding
    private lateinit var imageCaptcha: ImageCaptcha
    private lateinit var callBack: CallBack
    private lateinit var mainDataSource: MainDataSource<ApiService>

    override val layoutId: Int
        get() = R.layout.df_image_captcha

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): ImageCaptchaDF {
        this.mainDataSource = mainDataSource
        return this
    }

    fun setCallBack(callBack: CallBack): ImageCaptchaDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfImageCaptchaBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_ImageCaptchaDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.code = ""
        binding.codeUrl = ""
        findImageCaptcha()
    }

    /**
     * 更新图片验证码
     */
    fun changeImage(view: View) {
        findImageCaptcha()
    }

    fun sure(view: View) {
        callBack.success(imageCaptcha, binding.code!!)
        dismiss()
    }

    fun cancel(view: View) {
        callBack.fail()
        dismiss()
    }

    private fun findImageCaptcha(){
        mainDataSource.enqueueLoading({findImageCaptcha()},"获取图片验证码..."){
            onSuccess {
                imageCaptcha = it
                binding.codeUrl = imageCaptcha.imageCaptchaUrl
            }
        }
    }

    interface CallBack {
        fun success(imageCaptcha: ImageCaptcha, code: String)
        fun fail()
    }
}
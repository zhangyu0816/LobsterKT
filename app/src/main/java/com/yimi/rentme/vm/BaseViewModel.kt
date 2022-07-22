package com.yimi.rentme.vm

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import com.yimi.rentme.ApiService
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.inputMethodManager
import com.zb.baselibs.vm.BaseLibsViewModel

abstract class BaseViewModel : BaseLibsViewModel() {

    lateinit var imm: InputMethodManager
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null
    private var pvhR: PropertyValuesHolder? = null
    private var pvh: ObjectAnimator? = null
    private var exitTime = 0L
    var isScroll = false
    private val mHandler = Handler()

    abstract fun initViewModel()

    open val mainDataSource by lazy {
        MainDataSource(this, ApiService::class.java)
    }

    open fun back(view: View) {

    }

    open fun right(view: View) {

    }

    open fun checkPermissionGranted(vararg permissions: String): Boolean {
        var flag = true
        for (p in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    BaseApp.context, p
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                flag = false
                break
            }
        }
        return flag
    }

    /**
     * 打开键盘
     *
     * @param v
     */
    open fun showKeyBoard(v: View) {
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        v.requestFocus()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    /**
     * 隐藏键盘
     */
    open fun hintKeyBoard(view: View) {
        //拿到InputMethodManager
        imm = activity.inputMethodManager()
        //如果window上view获取焦点 && view不为空
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    open fun hintKeyBoard2() {
        //拿到InputMethodManager
        imm = activity.inputMethodManager()
        //如果window上view获取焦点 && view不为空
        if (imm.isActive && activity.currentFocus != null) {
            //拿到view的token 不为空
            if (activity.currentFocus!!.windowToken != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    open fun fitComprehensiveScreen() {
        activity.window.addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN) // 导致华为手机模糊
        activity.window.addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 导致华为手机黑屏
        activity.window.addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        activity.window.navigationBarColor = Color.TRANSPARENT
        activity.window.statusBarColor = Color.TRANSPARENT
    }


    open fun goAnimator(view: View?, min: Float, max: Float, time: Long) {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", min, max, min)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", min, max, min)
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(time)
        pvh!!.repeatCount = Animation.INFINITE
        pvh!!.start()
    }

    open fun stopGo() {
        if (pvh != null) pvh!!.cancel()
        pvh = null
    }

    /**
     * 视频页双击点赞
     */
    @SuppressLint("ClickableViewAccessibility")
    open fun initGood(clickView: View, imageView: View, ra: Runnable?, successRa: Runnable?) {
        imageView.rotation = 45f
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 2f, 1.8f, 2f, 2f, 3f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 2f, 1.8f, 2f, 2f, 3f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 1f, 1f, 0.5f, 0f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhSY, pvhSX, pvhA).setDuration(500)
        clickView.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!isScroll) {
                    if (System.currentTimeMillis() - exitTime > 500) {
                        exitTime = System.currentTimeMillis()
                        mHandler.postDelayed(ra!!, 500)
                    } else {
                        exitTime = 0
                        mHandler.removeCallbacks(ra!!)
                        imageView.x = motionEvent.x - ObjectUtils.getViewSizeByWidthFromMax(102)
                        imageView.y = motionEvent.y - ObjectUtils.getViewSizeByWidthFromMax(102)
                        imageView.alpha = 1f
                        if (pvh != null) pvh!!.start()
                        mHandler.postDelayed(successRa!!, 500)
                    }
                }
            }
            true
        }
    }
}
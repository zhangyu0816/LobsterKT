package com.yimi.rentme.vm

import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import com.yimi.rentme.ApiService
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.inputMethodManager
import com.zb.baselibs.vm.BaseLibsViewModel

abstract class BaseViewModel : BaseLibsViewModel() {

    lateinit var imm: InputMethodManager

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
}
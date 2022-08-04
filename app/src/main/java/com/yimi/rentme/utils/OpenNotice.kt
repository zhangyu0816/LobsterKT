package com.yimi.rentme.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.saveInteger

object OpenNotice {

    fun remindNotice(activity: AppCompatActivity) {
        if (getInteger("isNotificationEnabled") == 0 && isNotNotification(activity)) {
            saveInteger("isNotificationEnabled", 1)
            RemindDF(activity).setTitle("应用通知")
                .setContent(
                    "在您使用虾菇服务时，为了您能及时收到系统消息、聊天内容、关注、点赞等交互信息，虾菇集成了第三方推送服务（个推），此服务需要开通应用通知功能。" +
                            "若您点击“同意”按钮，将跳转到应用通知管理页面，选择开启通知及通知显示方式。" +
                            "若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法收到通知信息，不影响使用其他的虾菇功能/服务。" +
                            "您也可以通过“手机设置--应用--虾菇--通知”或app内“我的--设置--通知管理”，手动开启或关闭通知。"
                ).setSureName("同意").setCallBack(object :RemindDF.CallBack{
                    override fun sure() {
                        gotoSet(activity)
                    }
                }).show(activity.supportFragmentManager)
        }
    }

    fun isNotNotification(activity: AppCompatActivity): Boolean {
        var isOpened = false
        try {
            isOpened = NotificationManagerCompat.from(activity).areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return !isOpened
    }

    fun gotoSet(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, activity.applicationInfo.uid)
            activity.startActivity(intent)
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra("app_package", activity.packageName)
            intent.putExtra("app_uid", activity.applicationInfo.uid)
            activity.startActivity(intent)
        } else {
            // 其他
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        }
    }
}
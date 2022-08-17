package com.yimi.rentme.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.getLong
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

object LobsterObjectUtil {

    @JvmStatic
    fun isRightVisibility(right: String?): Boolean {
        if (right == null)
            return false
        return right == BaseApp.context.resources.getString(R.string.all_read)
    }

    @JvmStatic
    fun getBottleBgHeight(height: Float): Int {
        return (height * BaseApp.W / 1095f).toInt()
    }

    @JvmStatic
    fun cameraImageSelect(image: String): Boolean {
        var has = false
        for (item in MineApp.selectImageList) {
            if (item.imageUrl == image) {
                has = true
                break
            }
        }
        return has
    }

    @JvmStatic
    fun cameraImageSelectIndex(image: String): String {
        var index = ""
        for (item in MineApp.selectImageList) {
            if (item.imageUrl == image) {
                index = item.index.toString()
                break
            }
        }
        return index
    }

    @JvmStatic
    fun getImageHeight(scale: Float, width: Int, height: Int): Int {
        return (ObjectUtils.getViewSizeByWidth(scale) * height.toFloat() / width.toFloat()).toInt()
    }

    @JvmStatic
    fun getLogoHeight(scale: Float): Int {
        return (ObjectUtils.getViewSizeByWidth(scale) * 510f / 345f).toInt()
    }

    @JvmStatic
    fun getAge(birthday: String, age: Int): String {
        return DateUtil.getAge(birthday, age).toString()
    }

    @JvmStatic
    fun getTimeToToday(strDate: String): String {
        return DateUtil.getTimeToToday(strDate)
    }

    @JvmStatic
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getReward(position: Int): Drawable {
        return when (position) {
            0 -> BaseApp.context.resources.getDrawable(R.drawable.gradient_reward_1_radius20)
            1 -> BaseApp.context.resources.getDrawable(R.drawable.gradient_reward_2_radius20)
            else -> BaseApp.context.resources.getDrawable(R.drawable.gradient_reward_3_radius20)
        }
    }

    // 是不是视频
    @JvmStatic
    fun isVideo(filePath: String): Boolean {
        return filePath.contains(".mp4")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @JvmStatic
    fun getRanking(position: Int): Drawable? {
        return when (position) {
            0 -> BaseApp.context.resources.getDrawable(R.mipmap.reward_ranking_1)
            1 -> BaseApp.context.resources.getDrawable(R.mipmap.reward_ranking_2)
            else -> BaseApp.context.resources.getDrawable(R.mipmap.reward_ranking_3)
        }
    }

    // 超级喜欢
    @JvmStatic
    fun getSuperLikeRes(isPair: Boolean): Int {
        return if (isPair) R.mipmap.like_tag_icon else R.mipmap.super_like_small_icon
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @JvmStatic
    fun getNoData(position: Int, otherUserId: Long): Drawable {
        return if (position == 0) {
            if (otherUserId == 0L)
                BaseApp.context.resources.getDrawable(R.mipmap.no_anth_data)
            else
                BaseApp.context.resources.getDrawable(R.mipmap.no_other_anth_data)
        } else if (position == 1) {
            if (otherUserId == 0L)
                BaseApp.context.resources.getDrawable(R.mipmap.no_fan_data)
            else
                BaseApp.context.resources.getDrawable(R.mipmap.no_other_fan_data)
        } else if (position == 2) {
            BaseApp.context.resources.getDrawable(R.mipmap.no_belike_data)
        } else {
            BaseApp.context.resources.getDrawable(R.mipmap.no_visitor_data)
        }
    }

    @JvmStatic
    fun textName(hasLike: Boolean, isFollow: Boolean, index: Int, otherUserId: Long): String {
        return if (index == 2) {
            if (hasLike) "已喜欢" else "喜欢Ta"
        } else if (index == 1) {
            if (isFollow) "已关注" else if (otherUserId == 0L) "回粉" else "关注TA"
        } else {
            if (isFollow) "已关注" else "关注TA"
        }
    }

    @JvmStatic
    fun textColor(hasLike: Boolean, isFollow: Boolean, index: Int): Int {
        return if (index == 2) {
            if (hasLike) {
                BaseApp.context.resources.getColor(R.color.black_827)
            } else {
                BaseApp.context.resources.getColor(R.color.purple_7a4)
            }
        } else {
            if (isFollow) {
                BaseApp.context.resources.getColor(R.color.black_827)
            } else {
                BaseApp.context.resources.getColor(R.color.purple_7a4)
            }
        }
    }

    @JvmStatic
    fun getVipIntroBgHeight(scale: Float): Int {
        return (ObjectUtils.getViewSizeByWidth(scale) * 458f / 1035f).toInt()
    }

    @JvmStatic
    fun getVipIntroHeight(): Int {
        return (BaseApp.W * 3737f / 1125f).toInt()
    }

    @JvmStatic
    fun getGoodNum(goodNum: Int): String {
        return if (goodNum < 10000) goodNum.toString()
        else String.format("%.1f万", goodNum / 10000f)
    }

    @JvmStatic
    fun getReviews(reviews: Int): String {
        return if (reviews < 10000) reviews.toString()
        else String.format("%.1f万", reviews / 10000f)
    }

    @JvmStatic
    fun getChat(creationDate: String): String {
        return DateUtil.strToStr(creationDate, DateUtil.CN_yyyy_MM_dd_HH_mm_ss)
    }

    @JvmStatic
    fun isMine(fromId: Long): Boolean {
        return fromId == getLong("userId")
    }

    @JvmStatic
    fun judgeString(str: String): ArrayList<String> {
        val m = Pattern.compile(
            "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                    + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)"
        ).matcher(str)
        val url = ArrayList<String>()
        while (m.find()) {
            url.add(m.group())
        }
        return url
    }

    @JvmStatic
    fun getTitle(stanza: String, msgType: Int): String {
        //消息类型 1：文字 2：图片 3：语音 4：视频
        return if (msgType == 1)
            stanza
        else if (msgType == 2)
            "[图片]"
        else if (msgType == 3)
            "[语音]"
        else if (msgType == 4)
            "[视频]"
        else if (msgType == 112) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(stanza)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            assert(`object` != null)
            if (`object`!!.has("content"))
                `object`.optString("content")
            else
                "[你有新的动态消息]"
        } else if (msgType == 1000) {
            "每人发10句可以解锁资料哦~"
        } else "[暂不支付该类型消息]"
    }

    @JvmStatic
    fun count0_99(count: Int): String {
        return if (count == 0) "0" else if (count < 99) count.toString() else "99+"
    }

    @JvmStatic
    fun count99(count: Int): String {
        return if (count < 99) count.toString() else "99+"
    }
}
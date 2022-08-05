package com.yimi.rentme.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.bottle.BottleChatActivity
import com.zb.baselibs.app.BaseApp
import org.jetbrains.anko.startActivity
import org.json.JSONException
import org.json.JSONObject

class NoticeActivity:AppCompatActivity() {
    private var otherUserId: Long = 0
    private var discoverId: Long = 0
    private var driftBottleId: Long = 0
    private var flashTalkId: Long = 0
    private var cameraFilmResourceId: Long = 0
    private var dynType = 0
    private var reviewType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_notifivation)
        when (val activityContent = intent.getStringExtra("activityContent")) {
            "MainActivity" -> {
                BaseApp.context.startActivity<MainActivity>()
            }
            "ChatActivity" -> {
//                ActivityUtils.getChatActivity(intent.getLongExtra("otherUserId", 0), true)
            }
            else -> {
                try {
                    val `object` = JSONObject(activityContent)
                    val keys = `object`.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (key.contains("-")) {
                            val finalKey = key.split("-".toRegex()).toTypedArray()
                            val value = `object`.optString(key)
                            if ("Long" == finalKey[1]) {
                                if (TextUtils.equals(finalKey[0], "otherUserId")) {
                                    otherUserId = value.toLong()
                                } else if (TextUtils.equals(finalKey[0], "discoverId")) {
                                    discoverId = value.toLong()
                                } else if (TextUtils.equals(finalKey[0], "driftBottleId")) {
                                    driftBottleId = value.toLong()
                                } else if (TextUtils.equals(finalKey[0], "flashTalkId")) {
                                    flashTalkId = value.toLong()
                                } else if (TextUtils.equals(finalKey[0], "cameraFilmResourceId")) {
                                    cameraFilmResourceId = value.toLong()
                                }
                            }
                            if ("Integer" == finalKey[1]) {
                                if (TextUtils.equals(finalKey[0], "dynType")) {
                                    dynType = value.toInt()
                                } else if (TextUtils.equals(finalKey[0], "reviewType")) {
                                    reviewType = value.toInt()
                                }
                            }
                        }
                    }
                    val activity = `object`.optString("ActivityName")
                    if (TextUtils.equals(activity, "MemberDetailActivity")) {
                        BaseApp.context.startActivity<MemberDetailActivity>(
                            Pair("otherUserId",otherUserId)
                        )
                    } else if (TextUtils.equals(activity, "ChatActivity")) {
                        if (otherUserId == MineApp.systemUserId) {
//                            ActivityUtils.getMineSystemMsg()
                        } else {
//                            ActivityUtils.getChatActivity(otherUserId, true)
                        }
                    } else if (TextUtils.equals(activity, "FlashChatActivity")) {
//                        ActivityUtils.getFlashChatActivity(otherUserId, flashTalkId, true)
                    } else if (TextUtils.equals(activity, "DiscoverDetailActivity")) {
                        if (dynType == 1) {
                            BaseApp.context.startActivity<DiscoverDetailActivity>(
                                Pair("friendDynId", discoverId)
                            )
                        } else if (dynType == 2) {
                            BaseApp.context.startActivity<VideoDetailActivity>(
                                Pair("friendDynId", discoverId)
                            )
                        }
                    } else if (TextUtils.equals(activity, "NewsListActivity")) {
//                        ActivityUtils.getMineNewsList(reviewType)
                    } else if (TextUtils.equals(activity, "FCLActivity")) {
                        BaseApp.context.startActivity<FCLActivity>(
                            Pair("index", 1),
                            Pair("otherUserId", 0)
                        )
                    } else if (TextUtils.equals(activity, "BottleChatActivity")) {
                        BaseApp.context.startActivity<BottleChatActivity>(
                            Pair("msgChannelType", 2),
                            Pair("driftBottleId", driftBottleId),
                            Pair("isNotice",true)
                        )
                    } else if (TextUtils.equals(activity, "FilmResourceDetailActivity")) {
//                        ActivityUtils.getCameraFilmResourceDetail(cameraFilmResourceId, "")
                    }
                    finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
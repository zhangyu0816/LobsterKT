package com.yimi.rentme.vm

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.*
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.*
import com.yimi.rentme.databinding.AcBaseChatBinding
import com.yimi.rentme.dialog.BottleVipDF
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.roomdata.ChatListInfo
import com.yimi.rentme.roomdata.HistoryInfo
import com.yimi.rentme.utils.LobsterObjectUtil
import com.yimi.rentme.utils.OpenNotice
import com.yimi.rentme.utils.SoundUtil
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.luban.PhotoManager
import com.yimi.rentme.views.emoji.EmojiHandler
import com.yimi.rentme.views.emoji.EmojiUtil
import com.zb.baselibs.activity.WebActivity
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.mimc.CustomMessageBody
import com.zb.baselibs.mimc.UserManager
import com.zb.baselibs.utils.*
import com.zb.baselibs.utils.awesome.DownLoadUtil
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import java.io.File
import kotlin.math.min

class BaseChatViewModel : BaseViewModel(), OnRefreshListener {

    lateinit var binding: AcBaseChatBinding
    lateinit var adapter: BaseAdapter<HistoryInfo>
    private var allHistoryInfoList = ArrayList<HistoryInfo>()
    private val historyInfoList = ArrayList<HistoryInfo>()
    private var msgPageNo = 1
    private val pageSize = 20
    lateinit var emojiAdapter: BaseAdapter<Int>
    private val emojiList = ArrayList<Int>()
    private var dataList = ArrayList<String>()
    private var miUserId = ""
    private var historyMsgId = 0L
    var memberInfo = MemberInfo()
    var mineInfo = MineInfo()
    var isLockImage = true
    var isNotice = false
    var msgChannelType =
        0 // 1、普通聊天：common_otherUserId   2、漂流瓶：drift_driftBottleId   3、闪聊：flash_flashTalkId

    @SuppressLint("StaticFieldLeak")
    private lateinit var ivPlay: ImageView

    @SuppressLint("StaticFieldLeak")
    private lateinit var ivProgress: ImageView
    private var animator: ObjectAnimator? = null
    private var drawable: AnimationDrawable? = null// 语音播放

    @SuppressLint("StaticFieldLeak")
    private lateinit var preImageView: ImageView
    private var preDirection = 0
    private var soundPosition = -1
    private lateinit var soundUtil: SoundUtil
    private var isFirst = true
    private lateinit var photoManager: PhotoManager

    /** 普通聊天*/
    var otherUserId = 0L

    /** 漂流瓶*/
    var driftBottleId = 0L
    private lateinit var bottleInfo: BottleInfo

    /** 闪聊*/
    var flashTalkId = 0L
    private var myChatCount = 0
    private var otherChatCount = 0

    @SuppressLint("ClickableViewAccessibility")
    private val chatListTouch: View.OnTouchListener =
        View.OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE && (isSoftShowing() || binding.isEmoji) && isFirst) {
                isFirst = false
                hintKeyBoard2()
                binding.isEmoji = false
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                hintKeyBoard2()
                binding.isEmoji = false
                isFirst = true
            }
            false
        }

    @SuppressLint("ClickableViewAccessibility")
    private val speakTouch: View.OnTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                binding.tvSpeak.isPressed = true
                binding.tvSpeak.text = "松开结束"
                soundUtil.start()
                soundUtil.stopPlayer()
                stopVoiceDrawable()
                return@OnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                binding.audioBtn.visibility = View.GONE
                soundUtil.stop()
                binding.tvSpeak.text = "按住说话"
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewModel() {
        SoftHideKeyBoardUtil.assistActivity(activity, true)
        dataList.add("发送照片")
        dataList.add("发送小视频")
        binding.msgChannelType = msgChannelType
        binding.memberInfo = memberInfo
        mineInfo = MineApp.mineInfo
        binding.content = ""
        binding.refresh.setEnableLoadMore(false)
        binding.chatList.setOnTouchListener(chatListTouch)
        // 发送
        binding.edContent.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMsg(binding.tvSend)
            }
            true
        }
        binding.tvSpeak.setOnTouchListener(speakTouch)
        // 录音
        soundUtil = SoundUtil(activity).setAudioBtn(binding.audioBtn)
            .setCallBack(object : SoundUtil.CallBack {
                override fun playEnd(view: View) {
                    stopVoiceDrawable()
                    if (soundPosition != -1) adapter.notifyItemChanged(soundPosition)
                }

                override fun sendSound(resTime: Int, audioPath: String) {
                    uploadSound(File(audioPath), resTime)
                }

                override fun soundEnd() {
                    binding.tvSpeak.isPressed = false
                }
            })
        // 聊天记录
        adapter = BaseAdapter(activity, R.layout.item_history, historyInfoList, this)
        // 表情列表
        KeyboardStateObserver.getKeyboardStateObserver(activity)
            .setKeyboardVisibilityListener({ height ->
                binding.isVoice = false
                binding.isEmoji = false
                saveInteger("keyboardHeight", height)
                binding.emojiList.viewSize(BaseApp.W, height)
            }, false)

        binding.emojiList.viewSize(
            BaseApp.W,
            if (getInteger("keyboardHeight") == 0) BaseApp.H / 3 else getInteger("keyboardHeight")
        )
        for (i in 1 until EmojiHandler.maxEmojiCount) {
            emojiList.add(EmojiHandler.sCustomizeEmojisMap[i]!!)
        }
        emojiAdapter = BaseAdapter(activity, R.layout.item_emojj, emojiList, this)
        EmojiUtil.setProhibitEmoji(binding.edContent)

        // 提示开启通知
        if (OpenNotice.isNotNotification(activity)) {
            binding.showNotice = getInteger("chat_notice_${getLong("userId")}") == 0
            saveInteger("chat_notice_${getLong("userId")}", 1)
        }

        photoManager = PhotoManager(activity, mainDataSource) {
            UserManager.instance.sendMsg(
                miUserId, UserManager.PIC_FILE, 2, "", photoManager.jointWebUrl(","),
                0, "【图片】", driftBottleId, flashTalkId, msgChannelType
            )
            photoManager.deleteAllFile()
            MineApp.selectImageList.clear()
        }
        photoManager.isChat(true)
        BaseApp.fixedThreadPool.execute {
            val chatId = when (msgChannelType) {
                1 -> "common_$otherUserId"
                2 -> "drift_$driftBottleId"
                else -> "flash_$flashTalkId"
            }
            MineApp.nowChatId = chatId
            allHistoryInfoList.addAll(MineApp.historyDaoManager.getHistoryInfoList(chatId))
            activity.runOnUiThread {
                showLoading(Job(), "加载资料...")
                when (msgChannelType) {
                    1 -> {
                        isLockImage = false
                        binding.isLockImage = false
                        binding.bottomLayout.visibility =
                            if (otherUserId < 10010) View.GONE else View.VISIBLE
                        otherInfo()
                    }
                    2 -> {
                        isLockImage = MineApp.mineInfo.memberType == 1
                        binding.isLockImage = MineApp.mineInfo.memberType == 1
                        myBottle()
                    }
                    3 -> {
                        updateChatCount()
                        otherInfo()
                    }
                }
            }
        }
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        MineApp.nowChatId = ""
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        msgPageNo++
        updateHistoryInfoList()
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(view: View) {
        hintKeyBoard2()
        if (msgChannelType == 1) {
            if (otherUserId > 10010)
                activity.startActivity<MemberDetailActivity>(
                    Pair("otherUserId", otherUserId)
                )
        } else if (msgChannelType == 2) {
            if (MineApp.mineInfo.memberType == 2) {
                activity.startActivity<MemberDetailActivity>(
                    Pair("otherUserId", otherUserId)
                )
                return
            }
            if (MineApp.isFirstOpen)
                VipAdDF(activity).setMainDataSource(mainDataSource).setType(100)
                    .show(activity.supportFragmentManager)
            else
                BottleVipDF(activity).setMainDataSource(mainDataSource)
                    .show(activity.supportFragmentManager)
        } else if (msgChannelType == 3) {
            if (!binding.isLockImage)
                activity.startActivity<MemberDetailActivity>(
                    Pair("otherUserId", otherUserId)
                )
            else
                SCToastUtil.showToast(activity, "每人发10句可以解锁资料哦～", 2)
        }
    }

    /**
     * 关闭通知提示
     */
    fun closeNotice(view: View) {
        binding.showNotice = false
    }

    /**
     * 打开通知
     */
    fun openNotice(view: View) {
        OpenNotice.gotoSet(activity)
        binding.showNotice = false
    }

    /**
     * 显示语音键盘
     */
    fun toVoiceKeyboard(view: View) {
        hintKeyBoard2()
        binding.isEmoji = false
        if (!binding.isVoice) {
            if (checkPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                openAudio()
            } else {
                if (getInteger("audio_Permission") == 0) {
                    saveInteger("audio_Permission", 1)
                    RemindDF(activity).setTitle("权限说明")
                        .setContent(
                            "聊天室发送语音信息时需要使用录音功能，我们将会申请麦克风权限：" +
                                    "\n 1、申请麦克风权限--聊天时获取录制音频功能，" +
                                    "\n 2、若您点击“同意”按钮，我们方可正式申请上述权限，以便开启录音功能，发送语音信息，" +
                                    "\n 3、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用语音聊天功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 4、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭麦克风权限。"
                        ).setSureName("同意").setCallBack(object : RemindDF.CallBack {
                            override fun sure() {
                                openAudio()
                            }
                        }).show(activity.supportFragmentManager)
                } else SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", 2)
            }
        } else {
            binding.isVoice = false
        }
    }

    /**
     * 显示键盘
     */
    fun toKeyboard(view: View) {
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        binding.isEmoji = false
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.findFocus()
        view.postDelayed({
            val inputManager = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, 0)
        }, 300)
    }

    /**
     * 显示相机
     */
    fun toCamera(view: View) {
        hintKeyBoard2()
        binding.isEmoji = false
        if (checkPermissionGranted(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
            )
        ) {
            toSelectImage()
        } else {
            if (getInteger("image_permission") == 0) {
                saveInteger("image_permission", 1)
                RemindDF(activity).setTitle("权限说明").setContent(
                    "在使用聊天功能发送图片、视频时，我们将会申请相机、存储、麦克风权限：" +
                            "\n 1、申请相机权限--聊天时获取拍摄照片，录制视频功能，" +
                            "\n 2、申请存储权限--聊天时获取保存和读取图片、视频，" +
                            "\n 3、申请麦克风权限--聊天时获取录制视频音频功能，" +
                            "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便发送图片、视频等聊天内容，" +
                            "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法发送图片、视频等聊天内容，不影响使用其他的虾菇功能/服务，" +
                            "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。"
                ).setSureName("同意").setCallBack(object : RemindDF.CallBack {
                    override fun sure() {
                        toSelectImage()
                    }
                }).show(activity.supportFragmentManager)
            } else {
                if (!checkPermissionGranted(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", 2)
                } else if (!checkPermissionGranted(Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", 2)
                } else if (!checkPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                    SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", 2)
                }
            }
        }
    }

    /**
     * 显示表情
     */
    fun toEmoji(view: View) {
        if (binding.isEmoji) {
            binding.isEmoji = false
        } else {
            binding.isVoice = false
            hintKeyBoard2()
            BaseApp.fixedThreadPool.execute {
                SystemClock.sleep(300)
                activity.runOnUiThread {
                    binding.edContent.isFocusable = true
                    binding.edContent.isFocusableInTouchMode = true
                    binding.edContent.requestFocus()
                    binding.edContent.findFocus()
                    binding.isEmoji = true
                }
            }
        }
    }

    /**
     * 发送消息
     */
    fun sendMsg(view: View) {
        if (msgChannelType == 2) {
            if (bottleInfo.destroyType != 0) {
                SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", 2)
                return
            }
        }
        if (binding.content!!.trim().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入回复内容", 2)
            return
        }
        closeImplicit(binding.edContent)
        UserManager.instance.sendMsg(
            miUserId, UserManager.TEXT, 1, binding.content!!, "",
            0, "【文字】", driftBottleId, flashTalkId, msgChannelType
        )
        binding.content = ""
    }

    /**
     * 输入表情
     */
    fun addEmoji(position: Int, emoji: Int) {
        @SuppressLint("DefaultLocale")
        val content: String = binding.content + String.format("{f:%d}", position + 1)
        binding.edContent.setText(content)
        binding.edContent.setSelection(content.length)
    }

    /**
     * 删除内容
     */
    fun deleteContent(view: View) {
        binding.edContent.onKeyDown(
            KeyEvent.KEYCODE_DEL,
            KeyEvent(R.id.ed_content, KeyEvent.ACTION_DOWN)
        )
    }

    /**
     * 访问链接
     */
    fun toUrl(stanza: String) {
        val url = LobsterObjectUtil.judgeString(stanza)
        if (url.size > 0) {
            if (url.size == 1) {
                var url0 = url[0]
                if (!url0.contains("http")) {
                    url0 = "https://$url0"
                }
                activity.startActivity<WebActivity>(
                    Pair("webTitle", ""),
                    Pair("webUrl", url0)
                )
            } else {
                SelectorDF(activity).setDataList(url).setCallBack(object : SelectorDF.CallBack {
                    override fun sure(position: Int) {
                        var urlTemp = url[position]
                        if (!urlTemp.contains("http")) {
                            urlTemp = "https://$urlTemp"
                        }
                        activity.startActivity<WebActivity>(
                            Pair("webTitle", ""),
                            Pair("webUrl", urlTemp)
                        )
                    }
                }).show(activity.supportFragmentManager)
            }
        }
    }

    /**
     * 图片/视频
     */
    fun toImageVideo(view: View, historyInfo: HistoryInfo, direction: Int) {
        if (historyInfo.msgType == 2) {
            val sourceImageList = ArrayList<String>()
            sourceImageList.add(historyInfo.resLink)
            MyMNImage.setIndex(0).setSourceImageList(sourceImageList)
                .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                .setCallBack(object : ImageBrowserConfig.StartBack {
                    override fun onStartActivity() {
                        activity.startActivity<MNImageBrowserActivity>()
                    }
                })
                .imageBrowser()
        } else {
            if (direction == 0) {
                ivPlay = view.findViewById(R.id.iv_play)
                ivProgress = view.findViewById(R.id.iv_progress)
            } else {
                ivPlay = view.findViewById(R.id.iv_play_mine)
                ivProgress = view.findViewById(R.id.iv_progress_mine)
            }
            ivPlay.visibility = View.GONE
            BaseApp.fixedThreadPool.execute {
                val filePath = BaseApp.resFileDaoManager.getPath(historyInfo.resLink)
                if (filePath == null) {
                    DownLoadUtil.downLoad(
                        historyInfo.resLink, getVideoFile(), object : DownLoadUtil.CallBack {
                            override fun onFinish(filePath: String) {
                                ivPlay.visibility = View.VISIBLE
                                ivProgress.visibility = View.GONE
                                if (animator != null) animator!!.cancel()
                                animator = null
                                activity.startActivity<VideoPlayActivity>(
                                    Pair("videoUrl", filePath),
                                    Pair("videoType", 2),
                                )
                            }

                            override fun onProgress(progress: Long) {
                                animator =
                                    ObjectAnimator.ofFloat(ivProgress, "rotation", 0f, 360f)
                                        .setDuration(700)
                                animator!!.repeatMode = ValueAnimator.RESTART
                                animator!!.repeatCount = Animation.INFINITE
                                animator!!.start()
                                ivPlay.visibility = View.GONE
                                ivProgress.visibility = View.VISIBLE
                            }
                        })
                } else {
                    ivPlay.visibility = View.VISIBLE
                    ivProgress.visibility = View.GONE
                    if (animator != null) animator!!.cancel()
                    animator = null
                    activity.startActivity<VideoPlayActivity>(
                        Pair("videoUrl", filePath),
                        Pair("videoType", 2),
                    )
                }
            }
        }
    }

    /**
     * 播放语音
     */
    fun toVoice(view: View, historyInfo: HistoryInfo, direction: Int, position: Int) {
        BaseApp.fixedThreadPool.execute {
            val filePath = BaseApp.resFileDaoManager.getPath(historyInfo.resLink)
            if (filePath == null) {
                DownLoadUtil.downLoad(historyInfo.resLink, getDownloadFile(".amr"),
                    object : DownLoadUtil.CallBack {
                        override fun onFinish(filePath: String) {
                            historyInfo.isRead = 1
                            MineApp.historyDaoManager.updateRead(allHistoryInfoList[allHistoryInfoList.size - 1].thirdMessageId)
                            adapter.notifyItemChanged(position)
                            soundPosition = position
                            // direction 0 左  1右
                            stopVoiceDrawable()
                            val voiceView: ImageView = if (direction == 0) {
                                view.findViewById(R.id.iv_voice_left)
                            } else {
                                view.findViewById(R.id.iv_voice_right)
                            }
                            preImageView = voiceView
                            preDirection = direction

                            preImageView.setImageResource(if (direction == 0) R.drawable.voice_chat_anim_left else R.drawable.voice_chat_anim_right)
                            drawable = preImageView.drawable as AnimationDrawable
                            drawable!!.start()

                            soundUtil.soundPlayer(filePath, view)
                        }
                    })
            } else {
                soundPosition = position
                // direction 0 左  1右
                stopVoiceDrawable()
                val voiceView: ImageView = if (direction == 0) {
                    view.findViewById(R.id.iv_voice_left)
                } else {
                    view.findViewById(R.id.iv_voice_right)
                }
                preImageView = voiceView
                preDirection = direction

                preImageView.setImageResource(if (direction == 0) R.drawable.voice_chat_anim_left else R.drawable.voice_chat_anim_right)
                drawable = preImageView.drawable as AnimationDrawable
                drawable!!.start()

                soundUtil.soundPlayer(filePath, view)
            }
        }
    }

    fun check(stanzaInfo: StanzaInfo) {
        if (stanzaInfo.link.contains("person_detail")) {
            activity.startActivity<MemberDetailActivity>(
                Pair(
                    "otherUserId",
                    stanzaInfo.link.replace("zw://appview/person_detail?userId=", "").toLong()
                )
            )
        } else {
            dynDetail(
                stanzaInfo.link.replace("zw://appview/dynamic_detail?friendDynId=", "")
                    .toLong()
            )
        }
    }

    private fun stopVoiceDrawable() {
        if (drawable != null) {
            soundUtil.stopPlayer()
            preImageView.setImageResource(if (preDirection == 0) R.mipmap.icon_voice_3_left else R.mipmap.icon_voice_3_right)
            drawable!!.stop()
            drawable = null
        }
    }

    /**
     * 开启录音
     */
    private fun openAudio() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.RECORD_AUDIO, rationale = "为了更好的提供服务，需要获取麦克风权限"
            )
            binding.isVoice = true
        }
    }

    /**
     * 更新是否显示毛玻璃
     */
    fun updateBlur() {
        if (msgChannelType == 2) {
            isLockImage = MineApp.mineInfo.memberType == 1
            adapter.notifyItemRangeChanged(0, historyInfoList.size)
            binding.isLockImage = MineApp.mineInfo.memberType == 1
        }
    }

    /**
     * 发送图片/照片
     */
    fun uploadImageList(data: ArrayList<SelectImage>) {
        if (MineApp.selectImageList[0].videoUrl.isNotEmpty()) {
            uploadVideo(MineApp.selectImageList[0])
        } else {
            photoManager.addFileUpload(0, File(MineApp.selectImageList[0].videoUrl))
        }
    }

    /**
     * 更新聊天
     */
    fun updateChat(body: CustomMessageBody) {
        if (body.mDriftBottleId != 0L) { // 漂流瓶
            BaseApp.fixedThreadPool.execute {
                var historyInfo = HistoryInfo()
                historyInfo = historyInfo.createBottleHistoryInfoFromBody(
                    body, otherUserId, body.mMsgChannelType, body.mDriftBottleId
                )
                MineApp.historyDaoManager.insert(historyInfo)
                allHistoryInfoList.add(historyInfo)
                updateTime()
                activity.runOnUiThread {
                    historyInfoList.add(allHistoryInfoList[allHistoryInfoList.size - 1])
                    adapter.notifyItemRangeChanged(0, historyInfoList.size)
                    binding.chatList.scrollToPosition(historyInfoList.size - 1)
                }
            }
        }
    }

    /**
     * 选择发布动态
     */
    private fun toSelectImage() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO, rationale = "为了更好的提供服务，需要获取相机权限、存储权限、麦克风权限"
            )
            SelectorDF(activity).setDataList(dataList).setCallBack(object : SelectorDF.CallBack {
                override fun sure(position: Int) {
                    if (position == 0) {
                        activity.startActivity<SelectImageActivity>()
                    } else {
                        activity.startActivity<SelectImageActivity>(
                            Pair("showVideo", true)
                        )
                    }
                }
            }).show(activity.supportFragmentManager)
        }
    }

    /**
     * 更新聊天熟练
     */
    private fun updateChatCount() {
        for (item in allHistoryInfoList) {
            if (item.fromId == getLong("userId"))
                myChatCount += 1
            else
                otherChatCount += 1
        }
        isLockImage = (myChatCount.coerceAtMost(10) + otherChatCount.coerceAtMost(10)) < 20
        binding.isLockImage = (myChatCount.coerceAtMost(10) + otherChatCount.coerceAtMost(10)) < 20
    }

    /**
     * 我的漂流瓶
     */
    private fun myBottle() {
        mainDataSource.enqueue({ myBottle(driftBottleId) }) {
            onSuccess {
                bottleInfo = it
                otherUserId =
                    if (bottleInfo.userId == getLong("userId")) bottleInfo.otherUserId else bottleInfo.userId
                otherInfo()
            }
        }
    }

    /**
     * 用户详情
     */
    private fun otherInfo() {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                memberInfo = it
                binding.memberInfo = it
                otherImAccountInfo()
                if (msgChannelType == 2) {
                    bottleHistoryMsgList(1)
                } else {

                }
            }
        }
    }

    /**
     * 他人的聊天账号
     */
    private fun otherImAccountInfo() {
        mainDataSource.enqueue({ otherImAccountInfo(otherUserId, 3) }) {
            onSuccess {
                miUserId = it.imUserId
            }
        }
    }

    /**
     * 漂流瓶历史记录
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bottleHistoryMsgList(pageNo: Int) {
        mainDataSource.enqueue({ bottleHistoryMsgList(otherUserId, driftBottleId, pageNo) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    for (item in it) {
                        if (MineApp.historyDaoManager.getHistoryInfo(item.thirdMessageId) == null) {
                            var historyInfo = HistoryInfo()
                            historyInfo = historyInfo.createBottleHistoryInfo(
                                item, otherUserId, 2, driftBottleId
                            )
                            MineApp.historyDaoManager.insert(historyInfo)
                        }
                    }
                    activity.runOnUiThread {
                        if (historyMsgId == 0L)
                            historyMsgId = it[0].id
                        bottleHistoryMsgList(pageNo + 1)
                    }
                }
            }
            onFailed {
                dismissLoading()
                if (historyMsgId > 0L)
                    readOverDriftBottleHistoryMsg()
                BaseApp.fixedThreadPool.execute {
                    allHistoryInfoList =
                        MineApp.historyDaoManager.getHistoryInfoList("drift_$driftBottleId") as ArrayList<HistoryInfo>
                    val historyInfo = HistoryInfo()
                    historyInfo.thirdMessageId = "1"
                    historyInfo.fromId = bottleInfo.userId
                    historyInfo.toId = bottleInfo.otherUserId
                    historyInfo.creationDate = bottleInfo.createTime
                    historyInfo.stanza = bottleInfo.text
                    historyInfo.msgType = 1
                    historyInfo.title = "【文字】"
                    historyInfo.msgChannelType = 2
                    historyInfo.driftBottleId = driftBottleId
                    historyInfo.otherUserId = bottleInfo.userId
                    historyInfo.chatId = "drift_$driftBottleId"
                    historyInfo.mainUserId = getLong("userId")
                    allHistoryInfoList.add(historyInfo)
                    updateTime()
                    if (allHistoryInfoList.size > 0) {
                        var chatListInfo =
                            MineApp.chatListDaoManager.getChatListInfo("drift_$driftBottleId")
                        if (chatListInfo == null) {
                            chatListInfo = ChatListInfo()
                            chatListInfo.chatId = "drift_$driftBottleId"
                            chatListInfo.otherUserId = otherUserId
                            chatListInfo.nick = binding.memberInfo!!.nick
                            chatListInfo.image = binding.memberInfo!!.image
                            chatListInfo.creationDate = allHistoryInfoList[0].creationDate
                            chatListInfo.stanza = allHistoryInfoList[0].stanza
                            chatListInfo.msgType = allHistoryInfoList[0].msgType
                            chatListInfo.noReadNum = 0
                            chatListInfo.publicTag = ""
                            chatListInfo.effectType = 1
                            chatListInfo.authType = 1
                            chatListInfo.msgChannelType = 2
                            chatListInfo.chatType = 2
                            chatListInfo.mainUserId = getLong("userId")
                            MineApp.chatListDaoManager.insert(chatListInfo)
                        } else {
                            MineApp.chatListDaoManager.updateChatListInfo(
                                binding.memberInfo!!.nick,
                                binding.memberInfo!!.image,
                                allHistoryInfoList[0].creationDate,
                                allHistoryInfoList[0].stanza,
                                allHistoryInfoList[0].msgType,
                                0,
                                "drift_$driftBottleId"
                            )
                        }
                        activity.runOnUiThread {
                            msgPageNo = 1
                            historyInfoList.clear()
                            adapter.notifyDataSetChanged()
                            updateHistoryInfoList()
                        }
                    }
                }
            }
        }
    }

    /**
     * 清空漂流瓶消息
     */
    private fun readOverDriftBottleHistoryMsg() {
        if (bottleInfo.destroyType != 0) {
            return
        }
        mainDataSource.enqueue({
            readOverDriftBottleHistoryMsg(
                otherUserId, driftBottleId, historyMsgId
            )
        })
    }

    /**
     * 更新聊天列表
     */
    private fun updateHistoryInfoList() {
        if (pageSize * (msgPageNo - 1) < allHistoryInfoList.size) {
            val tempList = ArrayList<HistoryInfo>()
            for (i in pageSize * (msgPageNo - 1) until min(
                pageSize * msgPageNo, allHistoryInfoList.size
            )) {
                tempList.add(allHistoryInfoList[i])
            }
            tempList.reverse()
            historyInfoList.addAll(0, tempList)
            adapter.notifyItemRangeChanged(0, historyInfoList.size)
            binding.chatList.scrollToPosition(pageSize * (msgPageNo - 1))
        }
    }

    /**
     * 更新时间
     */
    private fun updateTime() {
        var time: String
        if (allHistoryInfoList.size > 0) {
            BaseApp.fixedThreadPool.execute {
                allHistoryInfoList[allHistoryInfoList.size - 1].showTime = true
                MineApp.historyDaoManager.updateShowTime(allHistoryInfoList[allHistoryInfoList.size - 1].thirdMessageId)
                time = allHistoryInfoList[allHistoryInfoList.size - 1].creationDate
                if (allHistoryInfoList.size > 1) {
                    for (i in allHistoryInfoList.size - 2 downTo 0) {
                        if (DateUtil.getDateCount(
                                allHistoryInfoList[i].creationDate, time,
                                DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 60f
                            ) > 3
                        ) {
                            time = allHistoryInfoList[i].creationDate
                            allHistoryInfoList[i].showTime = true
                            MineApp.historyDaoManager.updateShowTime(allHistoryInfoList[allHistoryInfoList.size - 1].thirdMessageId)
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传语音
     */
    private fun uploadSound(file: File, resTime: Int) {
        val requestFile: RequestBody = RequestBody.create("audio/mp3".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        mainDataSource.enqueue({
            uploadSound(
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file.name),
                body
            )
        }, false, "", MineApp.CHAT_URL) {
            onSuccess {
                UserManager.instance.sendMsg(
                    miUserId, UserManager.PIC_FILE, 3, "", it.url,
                    resTime, "【语音】", driftBottleId, flashTalkId, msgChannelType
                )
                soundUtil.setResTime(0)
            }
        }
    }

    /**
     * 判断已显示软键盘
     */
    private fun isSoftShowing(): Boolean {
        val rect = Rect()
        //获取当屏幕内容的高度
        val screenHeight = activity.window.decorView.height
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return screenHeight * 2 / 3 > rect.bottom
    }

    /**
     * 上传视频
     */
    private fun uploadVideo(selectImage: SelectImage) {
        val videoFile = File(selectImage.videoUrl)
        val requestFile: RequestBody =
            RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", videoFile.name, requestFile)
        mainDataSource.enqueueLoading(
            {
                uploadVideo(
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), videoFile.name),
                    body
                )
            }, "正在上传视频....", MineApp.CHAT_URL
        ) {
            onSuccess {
                UserManager.instance.sendMsg(
                    miUserId, UserManager.PIC_FILE, 4, "", it.url,
                    (selectImage.resTime / 1000).toInt(), "【视频】", driftBottleId,
                    flashTalkId, msgChannelType
                )
                MineApp.selectImageList.clear()
            }
        }
    }

    /**
     * 动态详情
     */
    private fun dynDetail(friendDynId: Long) {
        mainDataSource.enqueue({ dynDetail(friendDynId) }) {
            onSuccess {
                if (it.videoUrl.isEmpty())
                    activity.startActivity<DiscoverDetailActivity>(
                        Pair("friendDynId", it.friendDynId)
                    )
                else
                    activity.startActivity<VideoDetailActivity>(
                        Pair("friendDynId", it.friendDynId)
                    )
            }
        }
    }
}
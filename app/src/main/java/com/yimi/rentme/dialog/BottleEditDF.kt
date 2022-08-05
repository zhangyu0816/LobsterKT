package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.DfBottleEditBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil

class BottleEditDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfBottleEditBinding
    private var friendDynId = 0L
    private lateinit var mainDataSource: MainDataSource<ApiService>

    override val layoutId: Int
        get() = R.layout.df_bottle_edit

    fun setFriendDynId(friendDynId: Long): BottleEditDF {
        this.friendDynId = friendDynId
        return this
    }

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): BottleEditDF {
        this.mainDataSource = mainDataSource
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfBottleEditBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_BottleEditDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.9)
    }

    override fun initUI() {
        binding.dialog = this
        binding.edContent.typeface = MineApp.QingSongShouXieTiType
    }

    fun sure(view: View) {
        if (binding.edContent.text.toString().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入要回复的信息", 2)
            return
        }
        val map = HashMap<String, String>()
        map["friendDynId"] = friendDynId.toString()
        map["text"] = binding.edContent.text.toString()
        mainDataSource.enqueue({ dynDoReview(map) }) {
            onSuccess {
                SCToastUtil.showToast(activity, "回复成功", 2)
                dismiss()
            }
        }
    }
}
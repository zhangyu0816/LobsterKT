package com.yimi.rentme.vm

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMainBinding
import com.yimi.rentme.fragment.MainCardFrag
import com.yimi.rentme.fragment.MainChatFrag
import com.yimi.rentme.fragment.MainHomeFrag
import com.yimi.rentme.fragment.MainMineFrag
import com.zb.baselibs.views.replaceFragment

class MainViewModel : BaseViewModel() {

    lateinit var binding: AcMainBinding

    override fun initViewModel() {
        selectIndex(0)
    }

    /**
     * 选择
     */
    fun selectIndex(index: Int) {
        when (index) {
            0 -> activity.replaceFragment(MainHomeFrag(), R.id.main_content)
            1 -> activity.replaceFragment(MainCardFrag(), R.id.main_content)
            2 -> activity.replaceFragment(MainChatFrag(), R.id.main_content)
            3 -> activity.replaceFragment(MainMineFrag(), R.id.main_content)
        }
        binding.index = index
    }
}
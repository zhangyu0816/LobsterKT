package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoginBinding
import com.yimi.rentme.fragment.LoginNameFrag
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.views.addFragment

class LoginViewModel : BaseViewModel() {

    lateinit var binding: AcLoginBinding
    lateinit var fragment: BaseFragment

    override fun initViewModel() {
        binding.right = ""
        fragment = LoginNameFrag()
        activity.addFragment(fragment, R.id.login_content)
    }

    override fun back(view: View) {
        activity.finish()
    }
}
package com.yimi.rentme

import com.yimi.rentme.bean.CommonSwitch
import com.zb.baselibs.bean.HttpWrapBean
import retrofit2.http.GET

interface ApiService {

    // 功能开关
    @GET("api/AppCommon_functionSwitch")
    suspend fun functionSwitch(): HttpWrapBean<CommonSwitch>
}
package com.yimi.rentme

import com.yimi.rentme.bean.*
import com.zb.baselibs.bean.HttpWrapBean
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // 上传图片
    @Multipart
    @POST("YmUpload_image")
    suspend fun uploadImages(
        @Part("isCompre") isCompre: RequestBody,
        @Part("isCutImage") isCutImage: RequestBody,
        @Part("fileFileName") fileName: RequestBody,
        @Part("fileContentType") fileContentType: RequestBody,
        @Part file: MultipartBody.Part
    ): HttpWrapBean<ResourceUrl>

    // 功能开关
    @GET("api/AppCommon_functionSwitch")
    suspend fun functionSwitch(): HttpWrapBean<CommonSwitch>

    // 图片验证码
    @GET("api/ImageCaptca_findImageCaptcha")
    suspend fun findImageCaptcha(): HttpWrapBean<ImageCaptcha>

    // 我的信息
    @GET("api/Member_myInfo")
    suspend fun myInfo(): HttpWrapBean<MineInfo>

    // 修改个人信息
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfo")
    suspend fun modifyMemberInfo(@FieldMap map: Map<String, String>): HttpWrapBean<Any?>

    // 快捷登录
    @FormUrlEncoded
    @POST("api/Union_loginByUnionV2")
    suspend fun loginByUnion(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 用户注册验证码
    @FormUrlEncoded
    @POST("api/Login_registCaptcha")
    suspend fun registerCaptcha(@Field("userName") userName: String): HttpWrapBean<Any?>

    // 验证 注册验证码
    @FormUrlEncoded
    @POST("api/Login_verifyCaptcha")
    suspend fun verifyCaptcha(
        @Field("userName") userName: String,
        @Field("captcha") captcha: String
    ): HttpWrapBean<Any?>

    // 获取绑定手机号短信验证码
    @FormUrlEncoded
    @POST("api/Union_banderCaptcha")
    suspend fun banderCaptcha(
        @Field("userName") userName: String,
        @Field("imageCaptchaToken") imageCaptchaToken: String,
        @Field("imageCaptchaCode") imageCaptchaCode: String
    ): HttpWrapBean<Any?>

    // 绑定手机号
    @FormUrlEncoded
    @POST("api/Union_bindingPhone")
    suspend fun bindingPhone(
        @Field("userName") userName: String,
        @Field("captcha") captcha: String
    ): HttpWrapBean<Any?>

    // 验证 手机号是否注册
    @FormUrlEncoded
    @POST("api/Login_checkUserName")
    suspend fun checkUserName(@Field("userName") userName: String): HttpWrapBean<CheckUser>

    // 用户登录验证码
    @FormUrlEncoded
    @POST("api/Login_loginCaptcha")
    suspend fun loginCaptcha(@Field("userName") userName: String): HttpWrapBean<Any?>

    // 根据验证码登录
    @FormUrlEncoded
    @POST("api/Login_captchaLogin")
    suspend fun loginByCaptcha(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 根据密码登录
    @FormUrlEncoded
    @POST("api/Login_login")
    suspend fun loginByPass(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 用户注册
    @FormUrlEncoded
    @POST("api/Login_regist")
    suspend fun register(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 更新设备信息
    @FormUrlEncoded
    @POST("api/Login_modifyPushInfo")
    suspend fun modifyPushInfo(
        @Field("deviceCode") deviceCode: String,
        @Field("channelId") channelId: String,
        @Field("deviceHardwareInfo") deviceHardwareInfo: String,
        @Field("usePl") usePl: Int
    ): HttpWrapBean<Any?>

    // 我关注的人的动态列表
    @FormUrlEncoded
    @POST("api/Interactive_attentionDyn")
    suspend fun attentionDyn(
        @Field("pageNo") pageNo: Int,
        @Field("timeSortType") timeSortType: Int
    ): HttpWrapBean<ArrayList<DiscoverInfo>>

    // 获取他人信息
    @FormUrlEncoded
    @POST("api/Contact_otherInfo")
    suspend fun otherInfo(@Field("otherUserId") otherUserId: Long): HttpWrapBean<MemberInfo>
}
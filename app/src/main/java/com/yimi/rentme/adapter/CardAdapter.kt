package com.yimi.rentme.adapter

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.yimi.rentme.R
import com.yimi.rentme.bean.PairInfo
import com.yimi.rentme.views.card.BaseCardAdapter
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.adapter.initAdapter
import com.zb.baselibs.adapter.loadImage
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.ObjectUtils

class CardAdapter(private val activity: AppCompatActivity, private val viewModel: BaseViewModel) :
    BaseCardAdapter<PairInfo>() {
    private var dataList = ArrayList<PairInfo>()

    override val count: Int
        get() = dataList.size

    override val cardLayoutId: Int
        get() = R.layout.item_pair

    fun setDataList(dataList: ArrayList<PairInfo>) {
        this.dataList = dataList
    }

    override fun onBindData(position: Int, cardview: View) {
        val item = dataList[position]
        val ivPhoto = cardview.findViewById<ImageView>(R.id.iv_photo)
        val tvNick = cardview.findViewById<TextView>(R.id.tv_nick)
        val ivReal = cardview.findViewById<ImageView>(R.id.iv_real)
        val ivVip = cardview.findViewById<ImageView>(R.id.iv_vip)
        val distanceLayout = cardview.findViewById<LinearLayout>(R.id.distance_layout)
        val tvDistance = cardview.findViewById<TextView>(R.id.tv_distance)
        val tvDistanceUnit = cardview.findViewById<TextView>(R.id.tv_distance_unit)
        val tvCity = cardview.findViewById<TextView>(R.id.tv_city)
        val tvDistrict = cardview.findViewById<TextView>(R.id.tv_district)
        val tvSex = cardview.findViewById<TextView>(R.id.tv_sex)
        val tvConstellations = cardview.findViewById<TextView>(R.id.tv_constellations)
        val tvPersonalitySign = cardview.findViewById<TextView>(R.id.tv_personality_sign)
        val imageList = cardview.findViewById<RecyclerView>(R.id.image_list)
        val tvLeft = cardview.findViewById<View>(R.id.tv_left)
        val tvRight = cardview.findViewById<View>(R.id.tv_right)

        loadImage(
            ivPhoto, item.imageList[item.position], 0, R.mipmap.empty_icon,
            ObjectUtils.getViewSizeByWidth(0.94f), ObjectUtils.getViewSizeByHeight(0.86f), false,
            10f, null, false, 0, false, 0f
        )

        tvNick.text = item.nick
        tvNick.maxWidth = ObjectUtils.getViewSizeByWidth(0.5f)
        ivReal.visibility = if (item.faceAttest == 1) View.VISIBLE else View.GONE
        ivVip.visibility = if (item.memberType == 2) View.VISIBLE else View.GONE
        distanceLayout.visibility = if (item.distance.isEmpty()) View.GONE else View.VISIBLE
        tvDistance.text =
            if (item.distance.isEmpty()) "0.0"
            else if (item.distance.toFloat() < 1000f)
                String.format("%.1f", item.distance.toFloat())
            else
                String.format("%.1f", item.distance.toFloat() / 1000f)
        tvDistanceUnit.text = if (item.distance.toFloat() > 1000f) "km" else "m"
        BaseApp.fixedThreadPool.execute {
            tvCity.text = BaseApp.cityDaoManager.getCityName(item.provinceId, item.cityId)
            tvDistrict.text =
                BaseApp.districtDaoManager.getDistrictName(item.cityId, item.districtId)
        }
        tvSex.text = if (item.sex == 0) "女" else "男"
        tvConstellations.text = DateUtil.getConstellations(item.birthday)
        tvPersonalitySign.text = item.personalitySign
        imageList.visibility = if (item.imageList.size == 0) View.GONE else View.VISIBLE
        imageList.viewSize(ObjectUtils.getViewSizeByWidth(0.6f), -2)

        val adapter = BaseAdapter(activity, R.layout.item_image, item.imageList, viewModel)
        adapter.setSelectIndex(0)
        initAdapter(imageList, adapter, 1, 0, 0, 0, false)

        tvLeft.setOnClickListener {
            if (item.position > 0) {
                val mPosition = item.position
                item.position = item.position - 1
                loadImage(
                    ivPhoto, item.imageList[item.position], 0, R.mipmap.empty_icon,
                    ObjectUtils.getViewSizeByWidth(0.94f), ObjectUtils.getViewSizeByHeight(0.86f),
                    false, 10f, null, false, 0,
                    false, 0f
                )
                adapter.setSelectIndex(item.position)
                adapter.notifyItemChanged(item.position)
                adapter.notifyItemChanged(mPosition)
            }
        }
        tvRight.setOnClickListener {
            if (item.position < item.imageList.size - 1) {
                val mPosition = item.position
                item.position = item.position + 1
                loadImage(
                    ivPhoto, item.imageList[item.position], 0, R.mipmap.empty_icon,
                    ObjectUtils.getViewSizeByWidth(0.94f), ObjectUtils.getViewSizeByHeight(0.86f),
                    false, 10f, null, false, 0,
                    false, 0f
                )
                adapter.setSelectIndex(item.position)
                adapter.notifyItemChanged(item.position)
                adapter.notifyItemChanged(mPosition)
            }
        }
    }
}
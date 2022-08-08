package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.AMap.OnCameraChangeListener
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.CameraPosition
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.LocationInfo
import com.yimi.rentme.databinding.AcSelectLocationBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getString
import com.zb.baselibs.utils.saveString
import org.simple.eventbus.EventBus

class SelectLocationViewModel : BaseViewModel(), OnGeocodeSearchListener {

    lateinit var binding: AcSelectLocationBinding
    var isPublish = false
    lateinit var adapter: BaseAdapter<LocationInfo>
    private val locationInfoList = ArrayList<LocationInfo>()
    private var mPosition = -1
    private lateinit var aMap: AMap
    private lateinit var tagLl: LatLng
    private lateinit var myLl: LatLng
    private var isSearch = false
    private lateinit var geocodeSearch: GeocodeSearch

    override fun initViewModel() {
        binding.title = "修改定位"
        binding.right = "确定"
        try {
            geocodeSearch = GeocodeSearch(activity)
            geocodeSearch.setOnGeocodeSearchListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val latitude = getString("latitude")
        val longitude = getString("longitude")
        myLl = if (latitude.isEmpty() || longitude.isEmpty()) LatLng(0.0, 0.0)
        else LatLng(latitude.toDouble(), longitude.toDouble())

        adapter = BaseAdapter(activity, R.layout.item_select_location, locationInfoList, this)

        binding.edKey.setOnEditorActionListener { arg0, arg1, arg2 ->
            if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                hintKeyBoard2()
                querySearchByTips(arg0.getText().toString())
            }
            false
        }
        aMap = binding.mapView.map
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLl)) //设置中心点
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f)) // 设置地图可视缩放大小
        aMap.setOnCameraChangeListener(object : OnCameraChangeListener {
            override fun onCameraChange(cameraPosition: CameraPosition) {}
            override fun onCameraChangeFinish(cameraPosition: CameraPosition) {
                if (!isSearch) {
                    tagLl = cameraPosition.target
                    querySearch()
                }
                isSearch = false
            }
        })
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun right(view: View) {
        super.right(view)
        if (mPosition == -1) {
            SCToastUtil.showToast(activity, "请选择地址", 2)
            return
        }
        val info = locationInfoList[mPosition]
        val latLonPoint = LatLonPoint(info.latitude, info.longitude)
        val query = RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP)
        //异步查询
        geocodeSearch.getFromLocationAsyn(query)
    }

    override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult, p1: Int) {
        val regeocodeAddress = regeocodeResult.regeocodeAddress
        val info = locationInfoList[mPosition]
        info.provinceName = regeocodeAddress.province
        info.cityName = if (TextUtils.equals(regeocodeAddress.province, "台湾省")) "台湾"
        else regeocodeAddress.city
        info.districtName = regeocodeAddress.district

        saveString("address", info.address)
        if (isPublish) {
            EventBus.getDefault().post(info.cityName, "lobsterCityName")
            activity.finish()
        } else {
            saveString("cityName", info.cityName)
            BaseApp.fixedThreadPool.execute {
                MineApp.provinceId = BaseApp.provinceDaoManager.getProvinceId(info.provinceName)
                MineApp.cityId = BaseApp.cityDaoManager.getCityId(MineApp.provinceId, info.cityName)
                MineApp.districtId =
                    BaseApp.districtDaoManager.getDistrictId(MineApp.cityId, info.districtName)
                activity.runOnUiThread {
                    val map = HashMap<String, String>()
                    map["latitude"] = info.latitude.toString()
                    map["longitude"] = info.longitude.toString()
                    map["provinceId"] = MineApp.provinceId.toString()
                    map["cityId"] = MineApp.cityId.toString()
                    map["districtId"] = MineApp.districtId.toString()
                    updatePairPool(map)
                }
            }
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult, p1: Int) {
    }

    fun selectPosition(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    /**
     * 附近列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun querySearchByTips(keyWord: String) {
        if (keyWord.isEmpty()) return
        val inputQuery = InputtipsQuery(keyWord, "")
        inputQuery.cityLimit = false
        val inputTips = Inputtips(activity, inputQuery)
        inputTips.setInputtipsListener { tipList: List<Tip>, rCode: Int ->
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (tipList.isNotEmpty()) {
                    mPosition = -1
                    isSearch = true
                    locationInfoList.clear()
                    adapter.setSelectIndex(-1)
                    adapter.notifyDataSetChanged()
                    for (i in tipList.indices) {
                        try {
                            aMap.moveCamera(
                                CameraUpdateFactory.changeLatLng(
                                    LatLng(tipList[i].point.latitude, tipList[i].point.longitude)
                                )
                            ) //设置中心点
                            break
                        } catch (ignored: java.lang.Exception) {
                        }
                    }
                    for (tipItem in tipList) {
                        //如果该条数据不是一个地点的数据，剔除
                        if (tipItem.poiID != null && tipItem.point == null) {
                            continue
                        }
                        val info = LocationInfo()
                        info.title = tipItem.name
                        info.address = tipItem.address
                        info.latitude = tipItem.point.latitude
                        info.longitude = tipItem.point.longitude
                        locationInfoList.add(info)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
        inputTips.requestInputtipsAsyn()
    }

    private fun querySearch() {
        val query = PoiSearch.Query("", "", "")
        query.pageSize = 10
        try {
            val search = PoiSearch(activity, query)
            search.bound =
                PoiSearch.SearchBound(LatLonPoint(tagLl.latitude, tagLl.longitude), 10000, true)
            search.setOnPoiSearchListener(object : OnPoiSearchListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onPoiSearched(poiResult: PoiResult?, i: Int) {
                    locationInfoList.clear()
                    adapter.setSelectIndex(-1)
                    adapter.notifyDataSetChanged()
                    if (poiResult != null) {
                        for (poi in poiResult.pois) {
                            val info = LocationInfo()
                            info.cityName = poi.cityName
                            info.title = poi.title
                            info.address = poi.snippet
                            info.latitude = poi.latLonPoint.latitude
                            info.longitude = poi.latLonPoint.longitude
                            locationInfoList.add(info)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onPoiItemSearched(poiItem: PoiItem, i: Int) {}
            })
            search.searchPOIAsyn()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 提交位置
     */
    private fun updatePairPool(map: HashMap<String, String>) {
        mainDataSource.enqueue({ updatePairPool(map) }) {
            onSuccess {
                EventBus.getDefault().post("选择全局城市", "lobsterCityName")
                activity.finish()
            }
        }
    }
}
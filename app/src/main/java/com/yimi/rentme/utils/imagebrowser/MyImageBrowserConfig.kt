package com.yimi.rentme.utils.imagebrowser

import com.yimi.rentme.bean.DiscoverInfo
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig

class MyImageBrowserConfig : ImageBrowserConfig() {
    var onDiscoverClickListener: OnDiscoverClickListener? = null
    var onDeleteListener: OnDeleteListener? = null
    var discoverInfo: DiscoverInfo? = null
}
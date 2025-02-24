package com.czf.dji.util

import androidx.lifecycle.MutableLiveData
import com.czf.dji.data.DJIToastResult

/**
 * Description :djiToastLD的存储和提供工具类，用于向其他ViewModel提供djiToastLD
 *
 * @author: Byte.Cai
 * date : 2022/6/16
 *
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
object DJIToastUtil {
    var dJIToastLD: MutableLiveData<DJIToastResult>? = null
}
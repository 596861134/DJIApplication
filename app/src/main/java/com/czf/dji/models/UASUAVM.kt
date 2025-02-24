package com.czf.dji.models

import androidx.lifecycle.MutableLiveData
import dji.v5.manager.aircraft.uas.AreaStrategy
import dji.v5.manager.aircraft.uas.UASRemoteIDManager
import dji.v5.manager.aircraft.uas.UASRemoteIDStatus
import dji.v5.manager.aircraft.uas.UASRemoteIDStatusListener
import com.czf.dji.util.ToastUtils

/**
 * Description :美国无人机远程识别VM
 *
 * @author: Byte.Cai
 *  date : 2022/8/3
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class UASUAVM : DJIViewModel() {
    val uasRemoteIDStatus = MutableLiveData<UASRemoteIDStatus>()

    private val uasRemoteIDStatusListener = UASRemoteIDStatusListener {
        uasRemoteIDStatus.postValue(it)
    }

    init {
        val error = UASRemoteIDManager.getInstance().setUASRemoteIDAreaStrategy(AreaStrategy.US_STRATEGY)
        error?.apply {
            ToastUtils.showToast(toString())
        }
    }

    fun addRemoteIdStatusListener() {
        UASRemoteIDManager.getInstance().addUASRemoteIDStatusListener(uasRemoteIDStatusListener)
    }

    fun clearRemoteIdStatusListener() {
        UASRemoteIDManager.getInstance().clearUASRemoteIDStatusListener()
    }
}
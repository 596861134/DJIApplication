package com.czf.dji.models

import androidx.lifecycle.MutableLiveData
import com.czf.dji.util.ToastUtils
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.aircraft.uas.AreaStrategy
import dji.v5.manager.aircraft.uas.OperatorRegistrationNumberStatus
import dji.v5.manager.aircraft.uas.OperatorRegistrationNumberStatusListener
import dji.v5.manager.aircraft.uas.UASRemoteIDManager
import dji.v5.manager.aircraft.uas.UASRemoteIDStatus
import dji.v5.manager.aircraft.uas.UASRemoteIDStatusListener

/**
 * Description :欧盟无人机远程识别VM
 *
 * @author: Byte.Cai
 *  date : 2022/6/27
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
open class UASEuropeanVM : DJIViewModel() {

    val uasRemoteIDStatus = MutableLiveData(UASRemoteIDStatus())
    val operatorRegistrationNumberStatus = MutableLiveData(OperatorRegistrationNumberStatus())
    val currentOperatorRegistrationNumber = MutableLiveData("")

    private val uasRemoteIDStatusListener = UASRemoteIDStatusListener {
        uasRemoteIDStatus.postValue(it)
    }

    private val operatorRegistrationNumberStatusListener = OperatorRegistrationNumberStatusListener {
        operatorRegistrationNumberStatus.postValue(it)
    }

    protected val uasRemoteIDManager: UASRemoteIDManager = UASRemoteIDManager.getInstance()

    init {
        val error = uasRemoteIDManager.setUASRemoteIDAreaStrategy(AreaStrategy.EUROPEAN_STRATEGY)
        error?.apply {
            ToastUtils.showToast(toString())
        }
    }

    fun setOperatorRegistrationNumber(number: String) {
        uasRemoteIDManager.setOperatorRegistrationNumber(number, object :
            CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                ToastUtils.showToast("Set Operator Registration Number Success")
            }

            override fun onFailure(error: IDJIError) {
                ToastUtils.showToast(error.toString())
            }
        })
    }

    fun getOperatorRegistrationNumber() {
        uasRemoteIDManager.getOperatorRegistrationNumber(object :
            CommonCallbacks.CompletionCallbackWithParam<String> {
            override fun onSuccess(str: String) {
                ToastUtils.showToast("Get Operator Registration Number Success")
                currentOperatorRegistrationNumber.postValue(str)
            }

            override fun onFailure(error: IDJIError) {
                ToastUtils.showToast(error.toString())
            }
        })
    }

    fun addOperatorRegistrationNumberStatusListener() {
        uasRemoteIDManager.addOperatorRegistrationNumberStatusListener(operatorRegistrationNumberStatusListener)
    }

    fun removeOperatorRegistrationNumberStatusListener() {
        uasRemoteIDManager.clearAllOperatorRegistrationNumberStatusListener()
    }

    fun addRemoteIdStatusListener() {
        uasRemoteIDManager.addUASRemoteIDStatusListener(uasRemoteIDStatusListener)
    }

    fun clearRemoteIdStatusListener() {
        uasRemoteIDManager.clearUASRemoteIDStatusListener()
    }
}

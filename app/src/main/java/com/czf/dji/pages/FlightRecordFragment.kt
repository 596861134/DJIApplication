package com.czf.dji.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.czf.dji.databinding.FragFlightRecordPageBinding
import com.czf.dji.models.FlightRecordVM
import com.czf.dji.util.Helper
import dji.v5.utils.common.DiskUtil

/**
 * ClassName : com.czf.dji.pages.FlightRecordFragment
 * Description : FlightRecordFragment
 * Author : daniel.chen
 * CreateDate : 2021/7/15 11:13 上午
 * Copyright : ©2021 DJI All Rights Reserved.
 */
class FlightRecordFragment : DJIFragment() {
    private val flightRecordVM: FlightRecordVM by activityViewModels()

    private lateinit var binding: FragFlightRecordPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragFlightRecordPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initBtnListsner()
    }

    private fun initView() {
        binding.recordTv.text = flightRecordVM.getFlightLogPath()
        binding.clogPathTv.text = flightRecordVM.getFlyClogPath()
    }

    fun initBtnListsner() {
        binding.btnOpenFlightRecordPath.setOnClickListener {
            var flightRecordPath = flightRecordVM.getFlightLogPath()
            if (!flightRecordPath.contains(DiskUtil.SDCARD_ROOT )){
                return@setOnClickListener
            }
            var  uriPath = flightRecordPath.substring(DiskUtil.SDCARD_ROOT.length + 1 , flightRecordPath.length - 1).replace("/" , "%2f")
            Helper.openFileChooser(uriPath , activity)

        }

        binding.btnGetFlightCompressedLogPath.setOnClickListener {
            var flyclogPath = flightRecordVM.getFlyClogPath()
            if (!flyclogPath.contains(DiskUtil.SDCARD_ROOT )){
                return@setOnClickListener
            }
            var uriPath =
                flyclogPath.substring(DiskUtil.SDCARD_ROOT.length + 1, flyclogPath.length - 1)
                    .replace("/", "%2f")
            Helper.openFileChooser(uriPath, activity)
        }
    }
}
package com.czf.dji.pages

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.czf.dji.R
import com.czf.dji.databinding.FragNetworkRtkPageBinding
import com.czf.dji.databinding.FragRecordBinding
import com.czf.dji.models.MegaphoneVM
import dji.v5.utils.common.ContextUtil

/**
 * Description : 录音Fragment
 * Author : daniel.chen
 * CreateDate : 2022/1/17 2:41 下午
 * Copyright : ©2021 DJI All Rights Reserved.
 */
class RecordFragment: DJIFragment() {
    private val megaphoneVM: MegaphoneVM by activityViewModels()
    private var recordStarted:Boolean = false

    private lateinit var binding: FragRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnListener()
    }

    private fun initBtnListener() {

        //点击触发录音
        binding.btnRecord.setOnClickListener {
            if (!recordStarted) {
                megaphoneVM.startRecord()
                binding.chronometer.base = SystemClock.elapsedRealtime()
                binding.chronometer.start()
                var colorStateList: ColorStateList? =
                    ContextCompat.getColorStateList(ContextUtil.getContext(), R.color.red)
//                btn_record.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
//                btn_record.backgroundTintList = colorStateList
                binding.btnRecord.imageTintMode = PorterDuff.Mode.SRC_ATOP
                binding.btnRecord.imageTintList = colorStateList
                recordStarted = true
            } else {
                megaphoneVM.stopRecord()
                binding.chronometer.stop()
                binding.chronometer.base = SystemClock.elapsedRealtime()
                var colorStateList: ColorStateList? =
                    ContextCompat.getColorStateList(ContextUtil.getContext(), R.color.green)
                binding.btnRecord.imageTintMode = PorterDuff.Mode.SRC_ATOP
                binding.btnRecord.imageTintList = colorStateList
                recordStarted = false
            }
        }

        binding.cbPlay.setOnCheckedChangeListener { _, isChecked ->
            megaphoneVM.isQuickPlay = isChecked
        }
    }
}
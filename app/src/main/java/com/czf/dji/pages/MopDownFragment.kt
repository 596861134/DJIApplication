package com.czf.dji.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.czf.dji.R
import com.czf.dji.data.PipelineAdapter
import com.czf.dji.databinding.FragMopDownPageBinding
import com.czf.dji.models.MopVM
import dji.sdk.keyvalue.value.mop.PipelineDeviceType
import dji.sdk.keyvalue.value.mop.TransmissionControlType

import java.util.ArrayList

/**
 * Description :
 *
 * @author: Byte.Cai
 *  date : 2023/2/22
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class MopDownFragment : DJIFragment() {
    private val mopVM: MopVM by viewModels()
    private var adapter: PipelineAdapter? = null

    private lateinit var binding: FragMopDownPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragMopDownPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()

    }

    private fun initView() {
        adapter = PipelineAdapter(context, ArrayList())
        binding.rcPipeline.layoutManager = LinearLayoutManager(context)
        binding.rcPipeline.adapter = adapter
        binding.rcPipeline.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

    }

    private fun getType(checkedRadioButtonId: Int): PipelineDeviceType {
        return when (checkedRadioButtonId) {
            R.id.rb_on_board -> PipelineDeviceType.ONBOARD
            R.id.rb_payload -> PipelineDeviceType.PAYLOAD
            else -> PipelineDeviceType.PAYLOAD
        }
    }

    private fun initListener() {
        mopVM.initListener()
        binding.tvConnect.setOnClickListener {
            val deviceType = getType(binding.rgMopType.checkedRadioButtonId)
            val transferType = if (binding.cbReliable.isChecked) TransmissionControlType.STABLE else TransmissionControlType.UNRELIABLE
            val id = binding.etChannelId.text.toString().trim().toInt()
            mopVM.connect(id, deviceType, transferType, true)
        }

        mopVM.pipelineMapLivData.observe(viewLifecycleOwner) {
            it.forEach { map ->
                adapter?.addItem(map.value)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mopVM.stopMop()
    }
}
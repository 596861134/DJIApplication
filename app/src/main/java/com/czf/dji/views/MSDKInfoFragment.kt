package com.czf.dji.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czf.dji.R
import com.czf.dji.databinding.FragMainTitleBinding
import com.czf.dji.pages.DJIFragment

/**
 * Class Description
 *
 * @author Hoker
 * @date 2021/5/11
 *
 * Copyright (c) 2021, DJI All Rights Reserved.
 */
open class MSDKInfoFragment : DJIFragment() {

    private lateinit var binding: FragMainTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMainTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMSDKInfo()
        initListener()
    }

    private fun initMSDKInfo() {
        msdkInfoVm.msdkInfo.observe(viewLifecycleOwner) {
            it?.let {
                val mainInfo = "MSDK Info:[Ver:${it.SDKVersion} BuildVer:${it.buildVer} Debug:${it.isDebug} ProductCategory:${it.packageProductCategory} LDMLicenseLoaded:${it.isLDMLicenseLoaded} ]"
                binding.msdkInfoTextMain.text = mainInfo
                val secondInfo = "Device:${it.productType} | Network:${it.networkInfo} | CountryCode:${it.countryCode} | FirmwareVer:${it.firmwareVer} | LDMEnabled:${it.isLDMEnabled}"
                binding.msdkInfoTextSecond.text = secondInfo
            }
        }
        msdkInfoVm.refreshMSDKInfo()

        msdkInfoVm.mainTitle.observe(viewLifecycleOwner) {
            it?.let {
                binding.includeTitle.titleTextView.text = it
            }
        }
    }

    private fun initListener() {
        binding.includeTitle.returnBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
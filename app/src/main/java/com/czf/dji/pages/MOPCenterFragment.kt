package com.czf.dji.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.czf.dji.R
import com.czf.dji.databinding.FragMopCenterPageBinding

/**
 * Description :
 *
 * @author: Byte.Cai
 *  date : 2023/2/22
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class MOPCenterFragment : DJIFragment() {

    private lateinit var binding: FragMopCenterPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragMopCenterPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btOpenMopDownloadPage.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_open_down_page)
        }

        binding.btOpenMopInterfacePage.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_open_mop_interface_page)

        }

    }
}
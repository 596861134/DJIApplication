package com.czf.dji.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.czf.dji.databinding.FragAppSilentlyUpgradePageBinding
import com.czf.dji.models.APPSilentlyUpgradeVM

class APPSilentlyUpgradeFragment : DJIFragment() {
    private val vm: APPSilentlyUpgradeVM by activityViewModels()

    private lateinit var binding: FragAppSilentlyUpgradePageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragAppSilentlyUpgradePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSilentlyUpgradePackage.setOnClickListener {
            vm.setAPPSilentlyUpgrade(requireContext())
        }
        binding.btnInstallTestApp.setOnClickListener {
            vm.installApkWithOutNotice(requireContext())
        }
    }
}
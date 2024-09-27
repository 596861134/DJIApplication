package com.czf.dji.pages

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.czf.dji.R
import com.czf.dji.databinding.FragDataProtectionPageBinding
import com.czf.dji.models.DataProtectionVm
import com.czf.dji.util.Helper
import dji.v5.utils.common.DiskUtil

class DataProtectionFragment : DJIFragment() {

    private val diagnosticVm: DataProtectionVm by activityViewModels()

    private lateinit var binding: FragDataProtectionPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragDataProtectionPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productImprovementSwitch.isChecked = diagnosticVm.isAgreeToProductImprovement()
        binding.productImprovementSwitch.setOnCheckedChangeListener { _: CompoundButton?,
            isChecked: Boolean ->
            diagnosticVm.agreeToProductImprovement(isChecked)
        }

        binding.msdkLogSwitch.isChecked = diagnosticVm.isLogEnable()
        binding.msdkLogSwitch.setOnCheckedChangeListener { _: CompoundButton?,
            isChecked: Boolean ->
            diagnosticVm.enableLog(isChecked)
        }

        binding.logPathTv.text = diagnosticVm.logPath()

        binding.btnOpenLogPath.setOnClickListener {
            val path = diagnosticVm.logPath()
            if (!path.contains(DiskUtil.SDCARD_ROOT)) {
                return@setOnClickListener
            }
            val uriPath = path.substring(DiskUtil.SDCARD_ROOT.length + 1, path.length - 1).replace("/", "%2f")
            Helper.openFileChooser(uriPath, activity)
        }

        binding.btnClearLog.setOnClickListener {
            val configDialog = requireContext().let {
                AlertDialog.Builder(it, dji.v5.core.R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
                    .setTitle(R.string.clear_msdk_log)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ad_confirm) { configDialog, _ ->
                        kotlin.run {
                            diagnosticVm.clearLog()
                            configDialog.dismiss()
                        }
                    }
                    .setNegativeButton(R.string.ad_cancel) { configDialog, _ ->
                        kotlin.run {
                            configDialog.dismiss()
                        }
                    }
                    .create()
            }
            configDialog.show()
        }

        binding.btnExportAndZipLog.setOnClickListener {
            checkPermission()
            diagnosticVm.zipAndExportLog()
        }
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION")
            startActivityForResult(intent, 0)
        }
    }
}

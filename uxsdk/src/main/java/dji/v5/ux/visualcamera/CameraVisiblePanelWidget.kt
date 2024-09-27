package dji.v5.ux.visualcamera

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import dji.sdk.keyvalue.value.common.CameraLensType
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.ux.R
import dji.v5.ux.core.base.ICameraIndex
import dji.v5.ux.core.base.widget.ConstraintLayoutWidget
import dji.v5.ux.databinding.UxsdkPanelCommonCameraBinding
import dji.v5.ux.databinding.UxsdkPanelNdvlBinding

open class CameraVisiblePanelWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayoutWidget<Any>(context, attrs, defStyleAttr),
    ICameraIndex {

    var mCameraIndex = ComponentIndexType.LEFT_OR_MAIN
    var mLensType = CameraLensType.CAMERA_LENS_ZOOM

    override fun getCameraIndex(): ComponentIndexType {
        return mCameraIndex
    }

    override fun getLensType(): CameraLensType {
        return mLensType
    }

    override fun updateCameraSource(cameraIndex: ComponentIndexType, lensType: CameraLensType) {
        mCameraIndex = cameraIndex
        mLensType = lensType
        binding.widgetCameraConfigIsoAndEi.updateCameraSource(cameraIndex,lensType)
        binding.widgetCameraConfigShutter.updateCameraSource(cameraIndex,lensType)
        binding.widgetCameraConfigAperture.updateCameraSource(cameraIndex,lensType)
        binding.widgetCameraConfigEv.updateCameraSource(cameraIndex,lensType)
        binding.widgetCameraConfigWb.updateCameraSource(cameraIndex,lensType)
        binding.widgetCameraConfigStorage.updateCameraSource(cameraIndex,lensType)

        //NDVI镜头下不支持这类操作
        binding.widgetCameraConfigIsoAndEi.visibility = if (lensType == CameraLensType.CAMERA_LENS_MS_NDVI) INVISIBLE else VISIBLE
        binding.widgetCameraConfigShutter.visibility = if (lensType == CameraLensType.CAMERA_LENS_MS_NDVI) INVISIBLE else VISIBLE
        binding.widgetCameraConfigAperture.visibility = if (lensType == CameraLensType.CAMERA_LENS_MS_NDVI) INVISIBLE else VISIBLE
        binding.widgetCameraConfigEv.visibility = if (lensType == CameraLensType.CAMERA_LENS_MS_NDVI) INVISIBLE else VISIBLE
        binding.widgetCameraConfigWb.visibility = if (lensType == CameraLensType.CAMERA_LENS_MS_NDVI) INVISIBLE else VISIBLE
    }

    private lateinit var binding: UxsdkPanelCommonCameraBinding

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val inflater = LayoutInflater.from(context)
        binding = UxsdkPanelCommonCameraBinding.inflate(inflater, this, true)
        if (background == null) {
            setBackgroundResource(R.drawable.uxsdk_background_black_rectangle)
        }
    }

    override fun reactToModelChanges() {
        //do nothing
    }
}
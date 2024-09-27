package com.czf.dji.pages

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.czf.dji.R
import com.czf.dji.data.MEDIA_FILE_DETAILS_STR
import com.czf.dji.databinding.FragMediafileDetailsBinding
import com.czf.dji.models.MediaDetailsVM
import dji.sdk.keyvalue.value.camera.MediaFileType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.MediaFileDownloadListener
import dji.v5.utils.common.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.czf.dji.util.ToastUtils

/**
 * @author feel.feng
 * @time 2022/04/25 9:18 下午
 * @description:
 */
class MediaFileDetailsFragment : DJIFragment(), View.OnClickListener {
    private val mediaDetailsVM: MediaDetailsVM by activityViewModels()
    var mediaFile: MediaFile? = null
    var mediaFileDir = "/mediafile"
    lateinit var image: ImageView

    private lateinit var binding: FragMediafileDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragMediafileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTransition()
        initView(view)

    }

    private fun initView(view: View) {
        image = view.findViewById(R.id.image) as ImageView
        mediaFile = arguments?.getSerializable(MEDIA_FILE_DETAILS_STR) as MediaFile
        image.setImageBitmap(mediaFile?.thumbNail)

        image.setOnClickListener(this)
        binding.previewFile.setOnClickListener(this)
        binding.downloadFile.setOnClickListener(this)
        binding.cancelDownload.setOnClickListener(this)
        binding.pullXmpFileData.setOnClickListener(this)
        binding.pullXmpCustomInfo.setOnClickListener(this)
    }


    private fun initTransition() {
        sharedElementEnterTransition = DetailsTransition()
        enterTransition = Fade()
        sharedElementReturnTransition = DetailsTransition()
        exitTransition = Fade()

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaFile?.release()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.preview_file -> {
                fetchPreview()
            }
            R.id.download_file -> {
                downloadFile()
            }
            R.id.cancel_download -> {
                cancleDownload()
            }

            R.id.image -> {
                if (mediaFile?.fileType == MediaFileType.MP4 || mediaFile?.fileType == MediaFileType.MOV) {
                    enterVideoPage()
                }
            }

            R.id.pull_xmp_file_data -> {
                pullXMPFileDataFromCamera()
            }

            R.id.pull_xmp_custom_info -> {
                pullXMPCustomInfoFromCamera()
            }
        }
    }

    private fun enterVideoPage() {
        Navigation.findNavController(image).navigate(
            R.id.video_play_page,
            bundleOf(
                MEDIA_FILE_DETAILS_STR to mediaFile
            ),
        )
    }

    private fun fetchPreview() {
        mediaFile?.pullPreviewFromCamera(object :
            CommonCallbacks.CompletionCallbackWithParam<Bitmap> {
            override fun onSuccess(t: Bitmap?) {

                AndroidSchedulers.mainThread().scheduleDirect {
                    //  image.setImageBitmap(t)
                    Glide.with(ContextUtil.getContext()).load(t).into(image)
                }
            }

            override fun onFailure(error: IDJIError) {
                LogUtils.e("MediaFile", "fetch preview failed$error")
            }

        })
    }

    private fun downloadFile() {
        val dirs: File = File(DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), mediaFileDir))
        if (!dirs.exists()) {
            dirs.mkdirs()
        }

        val filepath = DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), mediaFileDir + "/" + mediaFile?.fileName)
        val file: File = File(filepath)
        var offset: Long = 0L
        if (file.exists()) {
            offset = file.length();
        }
        val outputStream = FileOutputStream(file, true)
        val bos = BufferedOutputStream(outputStream)
        var beginTime = System.currentTimeMillis()
        mediaFile?.pullOriginalMediaFileFromCamera(offset, object : MediaFileDownloadListener {
            override fun onStart() {
                showProgress()
            }

            override fun onProgress(total: Long, current: Long) {
                updateProgress(offset, current, total)
            }

            override fun onRealtimeDataUpdate(data: ByteArray, position: Long) {
                try {
                    bos.write(data)
                    bos.flush()
                } catch (e: IOException) {
                    LogUtils.e("MediaFile", "write error" + e.message)
                }
            }

            override fun onFinish() {

                var spendTime = (System.currentTimeMillis() - beginTime)
                var speedBytePerMill: Float? = mediaFile?.fileSize?.div(spendTime.toFloat())
                var divs = 1000.div(1024 * 1024.toFloat());
                var speedKbPerSecond: Float? = speedBytePerMill?.times(divs)

                if (mediaFile!!.fileSize <= offset) {
                    ToastUtils.showToast(getString(R.string.already_download))
                } else {
                    ToastUtils.showToast(
                        getString(R.string.msg_download_compelete_tips) + "${speedKbPerSecond}Mbps"
                                + getString(R.string.msg_download_save_tips) + "${filepath}"
                    )
                }
                hideProgress()
                try {
                    outputStream.close()
                    bos.close()
                } catch (error: IOException) {
                    LogUtils.e("MediaFile", "close error$error")
                }
            }

            override fun onFailure(error: IDJIError?) {
                LogUtils.e("MediaFile", "download error$error")
            }

        })
    }

    private fun cancleDownload() {
        mediaFile?.stopPullOriginalMediaFileFromCamera(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                hideProgress()
            }

            override fun onFailure(error: IDJIError) {
                hideProgress()

            }
        })
    }

    private fun pullXMPFileDataFromCamera() {
        mediaFile?.pullXMPFileDataFromCamera(object :
            CommonCallbacks.CompletionCallbackWithParam<String> {
            override fun onSuccess(s: String) {
                ToastUtils.showToast(s)
            }

            override fun onFailure(error: IDJIError) {
                ToastUtils.showToast(error.toString())
            }
        })
    }

    private fun pullXMPCustomInfoFromCamera() {
        mediaFile?.pullXMPCustomInfoFromCamera(object :
            CommonCallbacks.CompletionCallbackWithParam<String> {
            override fun onSuccess(s: String) {
                ToastUtils.showToast(s)
            }

            override fun onFailure(error: IDJIError) {
                ToastUtils.showToast(error.toString())
            }
        })
    }

    fun showProgress() {
        binding.includeProgress.progressContainer.visibility = View.VISIBLE
    }

    fun updateProgress(offset: Long, currentsize: Long, total: Long) {
        val fullSize = offset + total;
        val downloadedSize = offset + currentsize
        binding.includeProgress.progressBar.max = fullSize.toInt()
        binding.includeProgress.progressBar.progress = downloadedSize.toInt()
        val data: Double = StringUtils.formatDouble((downloadedSize.toDouble() / fullSize.toDouble()))
        val result: String = StringUtils.formatDouble(data * 100, "#0").toString() + "%"
        binding.includeProgress.progressInfo.text = result
    }

    fun hideProgress() {
        binding.includeProgress.progressContainer.visibility = View.GONE
    }

}
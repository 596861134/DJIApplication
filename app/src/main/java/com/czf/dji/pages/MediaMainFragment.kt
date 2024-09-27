package com.czf.dji.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.czf.dji.R
import com.czf.dji.models.MediaVM


/**
 * @author feel.feng
 * @time 2022/04/19 5:04 下午
 * @description: 回放下载操作界面
 */
class MediaMainFragment : DJIFragment() {
    private val mediaVM: MediaVM by activityViewModels()
    var adapter: MediaListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_media_main_page, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
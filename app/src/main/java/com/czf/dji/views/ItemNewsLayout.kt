package com.czf.dji.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.czf.dji.R
import com.czf.dji.databinding.ItemNewsBinding

/**
 * Class Description
 *
 * @author Hoker
 * @date 2022/2/15
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class ItemNewsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private lateinit var binding: ItemNewsBinding

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ItemNewsBinding.inflate(inflater, this, true)
    }

    fun setTitle(title : String){
        binding.itemTitle.text = title
    }

    fun setDate(date : String){
        binding.itemDate.text = date
    }

    fun setDescription(description : String){
        binding.itemDescription.text = description
    }

    fun showAlert(isShow: Boolean) {
        if (isShow) {
            binding.viewAlert.visibility = View.VISIBLE
        } else {
            binding.viewAlert.visibility = View.GONE
        }
    }
}
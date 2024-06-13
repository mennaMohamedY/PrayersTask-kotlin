package com.example.prayers_task.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("setImgSrc")
fun loadImage(view: ImageView, src: Int, ) {
    view.setImageResource(src)
}
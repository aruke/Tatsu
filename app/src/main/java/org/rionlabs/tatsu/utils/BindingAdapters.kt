package org.rionlabs.tatsu.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("durationMinutes")
fun TextView.durationMinutes(durationInMinutes: Int) {
    text = TimeUtils.toDurationString(context, durationInMinutes)
}
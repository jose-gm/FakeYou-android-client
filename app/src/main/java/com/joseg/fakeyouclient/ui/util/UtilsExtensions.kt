package com.joseg.fakeyouclient.ui.util

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)
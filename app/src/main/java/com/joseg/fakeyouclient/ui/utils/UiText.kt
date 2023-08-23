package com.joseg.fakeyouclient.ui.utils

import androidx.annotation.StringRes

sealed class UiText {
    class TextResource(@StringRes val stringRes: Int) : UiText()
    class DynamicText(val text: String) : UiText()
}
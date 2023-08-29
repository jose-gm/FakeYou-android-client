package com.joseg.fakeyouclient.ui.models

import androidx.annotation.DrawableRes
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.ModelEmptyScreenBinding
import com.joseg.fakeyouclient.ui.utils.UiText

data class EmptyScreenEpoxyModel(
    private val title: UiText,
    private val message: UiText,
    @DrawableRes private val drawableRes: Int = R.drawable.ic_sound_wave_1
) : ViewBindingEpoxyModelWithHolder<ModelEmptyScreenBinding>(R.layout.model_empty_screen) {
    override fun ModelEmptyScreenBinding.bind() {
        titleTextview.text = when (title) {
            is UiText.TextResource -> root.context.getString(title.stringRes)
            is UiText.DynamicText -> title.text
        }
        messageTextview.text = when (message) {
            is UiText.TextResource -> root.context.getString(message.stringRes)
            is UiText.DynamicText -> message.text
        }
        imageview.setImageResource(drawableRes)
    }
}
package com.joseg.fakeyouclient.ui.component.epoxymodels

import androidx.annotation.DrawableRes
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelEmptyScreenBinding
import com.joseg.fakeyouclient.ui.shared.UiText

data class EmptyScreenEpoxyModel(
    private val title: UiText,
    private val message: UiText,
    @DrawableRes private val drawableRes: Int = R.drawable.ic_sound_wave_1
) : ViewBindingEpoxyModelWithHolder<EpoxyModelEmptyScreenBinding>(R.layout.epoxy_model_empty_screen) {
    override fun EpoxyModelEmptyScreenBinding.bind() {
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
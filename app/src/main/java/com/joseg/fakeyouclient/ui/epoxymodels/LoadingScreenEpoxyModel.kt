package com.joseg.fakeyouclient.ui.epoxymodels

import androidx.annotation.ColorRes
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.ModelLoadingScreenBinding

data class LoadingScreenEpoxyModel(
    @ColorRes private val color: Int = R.color.white
) : ViewBindingEpoxyModelWithHolder<ModelLoadingScreenBinding>(R.layout.model_loading_screen) {
    override fun ModelLoadingScreenBinding.bind() {

    }
}

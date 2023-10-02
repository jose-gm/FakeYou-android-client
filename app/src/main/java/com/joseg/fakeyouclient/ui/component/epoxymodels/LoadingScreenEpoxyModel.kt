package com.joseg.fakeyouclient.ui.component.epoxymodels

import androidx.annotation.ColorRes
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelLoadingScreenBinding

data class LoadingScreenEpoxyModel(
    @ColorRes private val color: Int = R.color.white
) : ViewBindingEpoxyModelWithHolder<EpoxyModelLoadingScreenBinding>(R.layout.epoxy_model_loading_screen) {
    override fun EpoxyModelLoadingScreenBinding.bind() {}
}

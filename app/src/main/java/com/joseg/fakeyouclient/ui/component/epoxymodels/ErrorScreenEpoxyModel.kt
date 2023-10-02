package com.joseg.fakeyouclient.ui.component.epoxymodels

import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelErrorScreenBinding

data class ErrorScreenEpoxyModel(
    private val onClick: () -> Unit
) : ViewBindingEpoxyModelWithHolder<EpoxyModelErrorScreenBinding>(R.layout.epoxy_model_error_screen) {
    override fun EpoxyModelErrorScreenBinding.bind() {
        tryAgainButton.setOnClickListener {
            onClick()
        }
    }
}
package com.joseg.fakeyouclient.ui.models

import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.ModelErrorScreenBinding

data class ErrorScreenEpoxyModel(
    private val onClick: () -> Unit
) : ViewBindingEpoxyModelWithHolder<ModelErrorScreenBinding>(R.layout.model_error_screen) {
    override fun ModelErrorScreenBinding.bind() {
        tryAgainButton.setOnClickListener {
            onClick()
        }
    }
}
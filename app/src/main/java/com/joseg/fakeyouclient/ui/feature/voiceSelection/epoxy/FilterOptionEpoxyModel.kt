package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import android.content.res.ColorStateList
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.FilterOptions
import com.joseg.fakeyouclient.databinding.EpoxyModelFilterOptionBinding
import com.joseg.fakeyouclient.ui.component.epoxymodels.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.ui.shared.UiText

data class FilterOptionEpoxyModel(
    private val label: UiText,
    @DrawableRes private val icon: Int,
    private val isSelected: Boolean,
    private val type: FilterOptions,
    private val onClick: (FilterOptions) -> Unit
) : ViewBindingEpoxyModelWithHolder<EpoxyModelFilterOptionBinding>(R.layout.epoxy_model_filter_option) {

    override fun EpoxyModelFilterOptionBinding.bind() {
        root.setOnClickListener {
            onClick(type)
        }

        labelTextview.text = when (val value = label) {
            is UiText.TextResource -> root.context.getString(value.stringRes)
            is UiText.DynamicText -> value.text
        }

        imageview.setImageDrawable(AppCompatResources.getDrawable(root.context, icon))
        if (isSelected)
            select()
        else
            unselect()
    }

    private fun EpoxyModelFilterOptionBinding.select() {
        root.setBackgroundResource(R.drawable.background_selected_filter_menu)
        val color = root.context.getColor(R.color.black)
        labelTextview.setTextColor(color)
        imageview.imageTintList = ColorStateList.valueOf(color)
    }

    private fun EpoxyModelFilterOptionBinding.unselect() {
        root.background = null
        val color = root.context.getColor(R.color.white)
        labelTextview.setTextColor(color)
        imageview.imageTintList = ColorStateList.valueOf(color)
    }
}
package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import androidx.appcompat.content.res.AppCompatResources
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.databinding.EpoxyModelVoiceSelectionItemBinding
import com.joseg.fakeyouclient.ui.component.epoxymodels.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.model.VoiceModel

data class VoiceModelEpoxyModel(
    private val voiceModel: VoiceModel,
    private val onClick: (VoiceModel) -> Unit
) : ViewBindingEpoxyModelWithHolder<EpoxyModelVoiceSelectionItemBinding>(R.layout.epoxy_model_voice_selection_item) {

    override fun EpoxyModelVoiceSelectionItemBinding.bind() {
        containerCardView.setOnClickListener {
            onClick(voiceModel)
        }
        voiceModelTextview.text = voiceModel.title
        ttsModelTypeTextview.text = voiceModel.ttsModelType
        modelCreatorTextview.text = voiceModel.creatorDisplayName
        flagImageView.setImageDrawable(
            AppCompatResources.getDrawable(root.context, voiceModel.ietfPrimaryLanguageSubtag.getFlagIconRes())
        )
        languageTagTextview.text = voiceModel.ietfPrimaryLanguageSubtag.name.uppercase()
        ratingTextview.text = voiceModel.userRatings.getFiveStarRatingScaleStringValue()
    }
}

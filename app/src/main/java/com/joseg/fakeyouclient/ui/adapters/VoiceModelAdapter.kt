package com.joseg.fakeyouclient.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.databinding.AdapterVoiceSelectionItemBinding
import com.joseg.fakeyouclient.model.VoiceModelCompact

class VoiceModelAdapter : RecyclerView.Adapter<VoiceModelViewHolder>() {

    private var voiceModels: List<VoiceModelCompact> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceModelViewHolder {
        val binding = AdapterVoiceSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoiceModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoiceModelViewHolder, position: Int) {
        holder.bind(voiceModels[position])
    }

    override fun getItemCount(): Int = voiceModels.size

    fun submitList(list: List<VoiceModelCompact>) {
        voiceModels = list
        notifyDataSetChanged()
    }
}

class VoiceModelViewHolder(
    private val binding: AdapterVoiceSelectionItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(voiceModel: VoiceModelCompact) {
        with(binding) {
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
}
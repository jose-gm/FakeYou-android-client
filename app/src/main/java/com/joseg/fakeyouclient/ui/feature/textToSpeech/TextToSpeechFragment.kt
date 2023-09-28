package com.joseg.fakeyouclient.ui.feature.textToSpeech

import  android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.buildSpannedString
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.data.worker.TtsRequestPollerWorker
import com.joseg.fakeyouclient.ui.shared.onError
import com.joseg.fakeyouclient.ui.shared.onSuccess
import com.joseg.fakeyouclient.databinding.FragmentTextToSpeechBinding
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.ui.component.message.SnackbarMessage
import com.joseg.fakeyouclient.ui.util.NetworkConnectivityInfo
import com.joseg.fakeyouclient.ui.util.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TextToSpeechFragment : Fragment() {

    private var _binding: FragmentTextToSpeechBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TextToSpeechViewModel by viewModels()
    private var savedVoiceModel: VoiceModel? = null
    @Inject lateinit var networkConnectivityInfo: NetworkConnectivityInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextToSpeechBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedVoiceModel = viewModel.getVoiceModel()

        savedVoiceModel?.let {
            binding.showSelectedVoiceModel(it)
        } ?: binding.showVoiceModelSelectionMessage()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    networkConnectivityInfo.isOnline.collect {
                        binding.speakButton.isEnabled = it
                    }
                }

                launch {
                    viewModel.submittedTtsRequestFlow.collect {
                        it.onSuccess { inferenceJobToken ->
                            TtsRequestPollerWorker.start(requireContext(), inferenceJobToken, savedVoiceModel?.title ?: "")
                        }.onError { throwable, errorMessageRes ->
                            Log.e("ttsRequest", throwable.stackTraceToString())
                            SnackbarMessage.displayErrorMessage(binding.speakButton, requireContext().getString(errorMessageRes))
                        }
                        binding.speakButton.hideProgressIndicator()
                    }
                }
            }
        }

        binding.selectedVoiceCardView.setOnClickListener {
            findNavController().navigate(R.id.action_textToSpeechFragment_to_voiceSelectionFragment)
        }

        binding.speakButton.setOnClickListener {
            if (isValid()) {
                if (!binding.speakButton.isInLoadingState()) {
                    binding.speakButton.showProgressIndicator()
                    savedVoiceModel?.let { voiceModel ->
                        viewModel.postTtsRequest(voiceModel.modelToken, binding.inferenceTextEditText.text.toString())
                    }
                    binding.inferenceTextEditText.setText("")
                }
            }
            else {
                SnackbarMessage.displayErrorMessage(it, createValidationErrorMessage())
            }
        }
    }

    private fun isValid(): Boolean = savedVoiceModel != null && !binding.inferenceTextEditText.text.isNullOrBlank()

    private fun MaterialButton.showProgressIndicator() {
        val progress = CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            centerRadius = this@showProgressIndicator.iconSize / 1.5f
            strokeWidth = 3.dpToPx()
            start()
        }
        icon = progress
        tag = true
    }

    private fun MaterialButton.hideProgressIndicator() {
        icon = AppCompatResources.getDrawable(context, R.drawable.ic_outline_volume_up_24)
        iconSize = 32.dpToPx().toInt()
        tag = false
    }

    private fun MaterialButton.isInLoadingState(): Boolean = tag == true

    private fun createValidationErrorMessage(): CharSequence = buildSpannedString {
        append(requireContext().getText(R.string.validation_message_1))
        append(" ")
        append(requireContext().getText(R.string.validation_message_connector))
        appendLine()
        append(requireContext().getText(R.string.validation_message_2).toString().lowercase())
        append(".")
    }

    private fun FragmentTextToSpeechBinding.showVoiceModelSelectionMessage() {
        message.isVisible = true
        voiceModelTextview.isGone = true
        flagImageView.isGone = true
        languageTagTextview.isGone = true
    }

    private fun FragmentTextToSpeechBinding.showSelectedVoiceModel(voiceModel: VoiceModel) {
        message.isGone = true
        voiceModelTextview.isVisible = true
        flagImageView.isVisible = true
        languageTagTextview.isVisible = true

        voiceModelTextview.text = voiceModel.title
        flagImageView.setImageDrawable(
            AppCompatResources.getDrawable(root.context, voiceModel.ietfPrimaryLanguageSubtag.getFlagIconRes())
        )
        languageTagTextview.text = voiceModel.ietfPrimaryLanguageSubtag.name.uppercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
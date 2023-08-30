package com.joseg.fakeyouclient.ui.feature.textToSpeech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.FragmentTextToSpeechBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextToSpeechFragment : Fragment() {

    private var _binding: FragmentTextToSpeechBinding? = null
    private val binding get() = _binding!!

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

        binding.selectedVoiceCardView.setOnClickListener {
            findNavController().navigate(R.id.action_textToSpeechFragment_to_voiceSelectionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
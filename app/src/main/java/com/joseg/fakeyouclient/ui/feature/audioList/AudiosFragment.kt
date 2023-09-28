package com.joseg.fakeyouclient.ui.feature.audioList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import com.joseg.fakeyouclient.databinding.FragmentAudiosBinding
import com.joseg.fakeyouclient.ui.feature.audioList.epoxy.AudiosEpoxyController
import com.joseg.fakeyouclient.ui.shared.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudiosFragment : Fragment() {

    private var _binding: FragmentAudiosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudiosViewModel by viewModels()
    private lateinit var controller: AudiosEpoxyController
    private lateinit var player: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudiosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun  onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = ExoPlayer.Builder(requireContext())
            .build()

        controller = AudiosEpoxyController(
            viewModel::updateAudioUiItemState,
            viewModel::isAudioDownloaded,
            viewModel::getAudioFilePath
        )
        controller.setPlayer(player)
        binding.recyclerView.setController(controller)
        binding.recyclerView.animation = null
        binding.recyclerView.itemAnimator = null

        var currentDataInController = controller.currentData

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audiosUiStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    currentDataInController = controller.currentData
                    controller.setData(uiState)
                }
        }

        controller.addModelBuildListener {
            val audiosUiState = viewModel.audiosUiStateFlow.value
            if (currentDataInController is UiState.Success
                && audiosUiState is UiState.Success) {

                if (audiosUiState.data.items.size >
                    (currentDataInController as UiState.Success).data.items.size) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        controller.savePlayingAudioPlaybackPositionState()
        player.stop()
        player.release()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
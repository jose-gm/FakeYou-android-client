package com.joseg.fakeyouclient.ui.feature.audioList

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.ExoPlayer
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.FragmentAudiosBinding
import com.joseg.fakeyouclient.ui.feature.MainActivity
import com.joseg.fakeyouclient.ui.feature.audioList.epoxy.AudiosEpoxyController
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.util.hideKeyboard
import com.joseg.fakeyouclient.ui.util.showKeyboard
import com.joseg.fakeyouclient.ui.util.textChangesAsFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudiosFragment : Fragment() {

    private var _binding: FragmentAudiosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudiosViewModel by viewModels()
    private lateinit var controller: AudiosEpoxyController
    private lateinit var player: ExoPlayer
    private lateinit var onBackPressedCallback: OnBackPressedCallback

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

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.toolbar.isToolbarInSearchMode()) {
                binding.toolbar.toggleToolbarSearchMode(ToolbarMode.NORMAL_MODE)
            } else {
                viewModel.deactivateMultiSelectAction()
            }
        }
        onBackPressedCallback.isEnabled = false

        configureSearchView()

        controller = AudiosEpoxyController(
            viewModel::updateAudioState,
            viewLifecycleOwner.lifecycleScope,
            viewModel::isAudioDownloaded,
            viewModel::getAudioFilePath,
            viewModel::startDownload,
            viewModel::selectedAudioItem
        )

        binding.recyclerView.setController(controller)
        binding.recyclerView.animation = null
        binding.recyclerView.itemAnimator = null

        var currentDataInController = controller.currentData

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.audiosUiStateFlow
                        .collect { uiState ->
                            currentDataInController = controller.currentData
                            controller.setData(uiState)
                        }
                }

                launch {
                    viewModel.isMultiSelectActionOnFlow.collect {
                        binding.toolbar.setMultiSelectActionOn(it)
                    }
                }
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

    private fun  Toolbar.configureToolbar() {
        // Default config
        val activity = (requireActivity() as MainActivity)
        if (activity.isBottomNavigationHidden())
            activity.slideOutBottomNavigationView()
        activity.window.statusBarColor = Color.TRANSPARENT
        title = requireContext().getString(R.string.audio)
        setBackgroundColor(Color.TRANSPARENT)
        navigationIcon = null
        setNavigationOnClickListener(null)
        setOnMenuItemClickListener(null)
        menu.clear()
        inflateMenu(R.menu.audios_fragment_toolbar_normal_mode_menu)
        setOnMenuItemClickListener {
            if (it.itemId == R.id.search) {
                toggleToolbarSearchMode(ToolbarMode.SEARCH_MODE)
                true
            } else { false }
        }

        // config based on the Toolbar mode
        onToolbarModeListener { mode ->
            when (mode) {
                ToolbarMode.SEARCH_MODE -> {
                    viewModel.blockMultiSelectAction()
                    onBackPressedCallback.isEnabled = true
                    title = ""
                    navigationIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)?.apply {
                        setTint(ContextCompat.getColor(requireContext(), R.color.white))
                    }

                    setNavigationOnClickListener {
                        this@AudiosFragment.hideKeyboard()
                        binding.toolbar.toggleToolbarSearchMode(ToolbarMode.NORMAL_MODE)
                    }
                }
                ToolbarMode.NORMAL_MODE -> {
                    viewModel.unblockMultiSelectAction()
                    title = requireContext().getString(R.string.audios)
                    navigationIcon = null
                    setNavigationOnClickListener(null)
                    onBackPressedCallback.isEnabled = false
                }
            }
        }
    }

    private fun Toolbar.configMultiSelectAction() {
        val activity = (requireActivity() as MainActivity)
        activity.slideInBottomNavigationView()
        activity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.toolbar_action_mode)
        title = ""
        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toolbar_action_mode))
        navigationIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)?.apply {
            setTint(ContextCompat.getColor(requireContext(), R.color.white))
        }

        setNavigationOnClickListener {
            viewModel.deactivateMultiSelectAction()
        }
        setOnMenuItemClickListener(null)
        menu.clear()
        inflateMenu(R.menu.audios_fragment_toolbar_multi_select_mode_menu)
        setOnMenuItemClickListener {
            if (it.itemId == R.id.delete) {
                viewModel.deleteSelectedAudios()
                true
            } else { false }
        }
    }

    private fun Toolbar.setMultiSelectActionOn(mode: Boolean) {
        if (mode) {
            onBackPressedCallback.isEnabled = true
            configMultiSelectAction()
        } else {
            onBackPressedCallback.isEnabled = false
            configureToolbar()
        }
    }

    private fun Toolbar.toggleToolbarSearchMode(mode: ToolbarMode) {
        val searchViewContainer = this.findViewById<LinearLayoutCompat>(R.id.searchView_container)
        val actionSearch = this.menu.findItem(R.id.search)
        val editText = this.findViewById<EditText>(R.id.search_textInputEdittext)

        when (mode) {
            ToolbarMode.NORMAL_MODE -> {
                editText.setText("")
                searchViewContainer.isGone = true
                actionSearch.isVisible = true
            }
            ToolbarMode.SEARCH_MODE -> {
                searchViewContainer.isVisible = true
                actionSearch.isVisible = false
                editText.requestFocus()
                this@AudiosFragment.showKeyboard()
            }
        }
    }

    private fun Toolbar.isToolbarInSearchMode(): Boolean =
        this.findViewById<LinearLayoutCompat>(R.id.searchView_container).isVisible &&
                !menu.findItem(R.id.search).isVisible

    private fun configureSearchView() {
        binding.searchTextInputEdittext.textChangesAsFlow()
            .onEach {
                if (it.isNullOrBlank())
                    binding.clearTextImageview.isGone = true
                else
                    binding.clearTextImageview.isVisible = true
            }
            .debounce(500)
            .onEach { viewModel.submitSearchQuery(it.toString()) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.clearTextImageview.setOnClickListener {
            binding.searchTextInputEdittext.setText("")
        }
    }

    // Observe if the Toolbar searchView is visible. If it's visible, this means the Toolbar is in
    // search mode.
    private fun Toolbar.onToolbarModeListener(callback: ((ToolbarMode) -> Unit)?) {
        binding.searchViewContainer.tag = binding.searchViewContainer.visibility
        val searchViewContainer = binding.searchViewContainer

        if (callback == null) {
            searchViewContainer.viewTreeObserver.addOnGlobalLayoutListener(null)
        } else {
            searchViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
                val visibility = searchViewContainer.visibility
                if (searchViewContainer.tag as? Int != visibility) {
                    searchViewContainer.tag = searchViewContainer.visibility

                    if (callback != null) {
                        callback(
                            when (searchViewContainer.visibility) {
                            View.VISIBLE -> ToolbarMode.SEARCH_MODE
                            else -> ToolbarMode.NORMAL_MODE
                        })
                    }
                }
            }
        }
    }

    private enum class ToolbarMode {
        NORMAL_MODE, SEARCH_MODE;
    }

    override fun onStart() {
        super.onStart()
        player = ExoPlayer.Builder(requireContext())
            .build()
        controller.setPlayer(player)
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
        if (::onBackPressedCallback.isInitialized)
            onBackPressedCallback.remove()
    }
}
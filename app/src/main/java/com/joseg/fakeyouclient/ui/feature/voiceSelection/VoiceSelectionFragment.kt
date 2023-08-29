package com.joseg.fakeyouclient.ui.feature.voiceSelection

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.FragmentVoiceModelSelectionBinding
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy.VoiceModelEpoxyController
import com.joseg.fakeyouclient.ui.utils.hideKeyboard
import com.joseg.fakeyouclient.ui.utils.showKeyboard
import com.joseg.fakeyouclient.ui.utils.textChangesAsFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceSelectionFragment : Fragment() {

    private var _binding: FragmentVoiceModelSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VoiceSelectionViewModel by viewModels()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceModelSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.toolbar.isToolbarInSearchMode()) {
                binding.toolbar.toggleToolbarSearchMode(ToolbarMode.NORMAL_MODE)
                this.isEnabled = false
            }
        }
        onBackPressedCallback.isEnabled = false

        configureToolbar()
        configureSearchView()

        val epoxyController = VoiceModelEpoxyController(
            onVoiceModelClick = ::onVoiceModelSelected,
            onRetry = viewModel::refresh
        )
        epoxyController.adapter.addModelBuildListener {
            binding.recyclerView.scrollToPosition(0)
        }
        binding.recyclerView.setController(epoxyController)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.voiceModelUiStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { voiceModelUiState ->
                    binding.swipeToRefresh.isRefreshing = false
                    epoxyController.setData(voiceModelUiState)
            }
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun configureToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setNavigationOnClickListener(null)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.toolbar.onToolbarModeListener { mode ->
            when (mode) {
                ToolbarMode.SEARCH_MODE -> {
                    binding.toolbar.setNavigationOnClickListener {
                        this@VoiceSelectionFragment.hideKeyboard()
                        binding.toolbar.toggleToolbarSearchMode(ToolbarMode.NORMAL_MODE)
                        binding.toolbar.setNavigationOnClickListener(null)
                    }
                }
                ToolbarMode.NORMAL_MODE -> {
                    onBackPressedCallback.isEnabled = false
                    binding.toolbar.setupWithNavController(navController, appBarConfiguration)
                }
            }
        }

        binding.toolbar.inflateMenu(R.menu.voice_selection_toolbar_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_filter -> {
                    FilterBottomSheetFragment().show(childFragmentManager, FilterBottomSheetFragment.TAG)
                    true
                }
                R.id.action_search -> {
                    binding.toolbar.toggleToolbarSearchMode(ToolbarMode.SEARCH_MODE)
                    onBackPressedCallback.isEnabled = true
                    true
                }
                else -> false
            }
        }
    }

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

    private fun onVoiceModelSelected(voiceModel: VoiceModel) {
        viewModel.submitVoiceModelSelection(voiceModel)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::onBackPressedCallback.isInitialized)
            onBackPressedCallback.remove()
    }

    private fun Toolbar.toggleToolbarSearchMode(mode: ToolbarMode) {
        val searchViewContainer = this.findViewById<LinearLayoutCompat>(R.id.searchView_container)
        val actionSearch = this.menu.findItem(R.id.action_search)
        val editText = this.findViewById<EditText>(R.id.search_textInputEdittext)

        when (mode) {
            ToolbarMode.NORMAL_MODE -> {
                editText.setText("")
                editText.clearFocus()
                searchViewContainer.isGone = true
                actionSearch.isVisible = true
            }
            ToolbarMode.SEARCH_MODE -> {
                searchViewContainer.isVisible = true
                actionSearch.isVisible = false
                editText.requestFocus()
                this@VoiceSelectionFragment.showKeyboard()
            }
        }
    }

    private fun Toolbar.isToolbarInSearchMode(): Boolean =
        this.findViewById<LinearLayoutCompat>(R.id.searchView_container).isVisible &&
                !this.menu.findItem(R.id.action_search).isVisible

    private fun Toolbar.onToolbarModeListener(callback: (ToolbarMode) -> Unit) {
        binding.searchViewContainer.tag = binding.searchViewContainer.visibility
        val searchViewContainer = binding.searchViewContainer

        searchViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            val visibility = searchViewContainer.visibility
            if (searchViewContainer.tag as? Int != visibility) {
                searchViewContainer.tag = searchViewContainer.visibility

                callback(when (searchViewContainer.visibility) {
                    View.VISIBLE -> ToolbarMode.SEARCH_MODE
                    else -> ToolbarMode.NORMAL_MODE
                })
            }
        }
    }

    private enum class ToolbarMode {
        NORMAL_MODE, SEARCH_MODE;
    }
}
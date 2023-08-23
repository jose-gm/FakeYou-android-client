package com.joseg.fakeyouclient.ui.feature.voiceSelection

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.joseg.fakeyouclient.common.onError
import com.joseg.fakeyouclient.common.onLoading
import com.joseg.fakeyouclient.common.onSuccess
import com.joseg.fakeyouclient.databinding.FragmentVoiceModelSelectionBinding
import com.joseg.fakeyouclient.ui.adapters.VoiceModelAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceSelectionFragment : Fragment() {

    private var _binding: FragmentVoiceModelSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VoiceSelectionViewModel by viewModels()
    private lateinit var filterBottomSheetFragment: FilterBottomSheetFragment

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

        configureToolbar()
        configureBottomSheet()
        configureSearchFilterBar()

        val adapter = VoiceModelAdapter()
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.voiceModelUiStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { voiceModelUiState ->
                voiceModelUiState.onSuccess {
                    adapter.submitList(it.voiceModels)
                    binding.contentProgressBar.hide()
                    binding.showContent()
                }.onLoading {
                    binding.hideContent()
                    binding.contentProgressBar.show()
                }.onError {
                    binding.hideContent()
                }
            }
        }
    }

    private fun configureToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun configureBottomSheet() {
        filterBottomSheetFragment = FilterBottomSheetFragment()

        binding.filterButton.setOnClickListener {
            filterBottomSheetFragment.show(childFragmentManager, "")
        }
    }

    private fun configureSearchFilterBar() {
        binding.searchTextInputEdittext.setOnFocusChangeListener { view, b ->
            when (b) {
                true -> binding.setSearchFilterBarFocus()
                false -> binding.removeSearchFilterBarFocus()
            }
        }

        binding.searchTextInputEdittext.addTextChangedListener(
            afterTextChanged = { text: Editable? ->
                viewModel.submitSearchQuery(text.toString())
            },
            onTextChanged = { text, start, before, count ->
                if (text.isNullOrBlank())
                    binding.clearTextImageview.isGone = true
                else
                    binding.clearTextImageview.isVisible = true
            }
        )

        binding.clearTextImageview.setOnClickListener {
            binding.searchTextInputEdittext.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentVoiceModelSelectionBinding.setSearchFilterBarFocus() {
    searchFilterContainer.setBackgroundResource(R.drawable.background_search_filter_bar_active)
}

fun FragmentVoiceModelSelectionBinding.removeSearchFilterBarFocus() {
    searchFilterContainer.setBackgroundResource(R.drawable.background_search_filter_bar_inactive)
}

fun FragmentVoiceModelSelectionBinding.hideContent() {
    searchFilterContainer.isGone = true
    recyclerView.isGone = true
}

fun FragmentVoiceModelSelectionBinding.showContent() {
    searchFilterContainer.isVisible = true
    recyclerView.isVisible = true
}
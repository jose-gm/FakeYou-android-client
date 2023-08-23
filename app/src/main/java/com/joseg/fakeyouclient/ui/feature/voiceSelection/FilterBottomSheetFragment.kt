package com.joseg.fakeyouclient.ui.feature.voiceSelection

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.onSuccess
import com.joseg.fakeyouclient.databinding.BottomSheetFilterBinding
import com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy.FilterMenuEpoxyController
import com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy.FilterMenuOptionItemEpoxyController
import kotlinx.coroutines.launch

class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VoiceSelectionViewModel by viewModels(ownerProducer = {requireParentFragment()})
    private lateinit var filterMenuEpoxyController: FilterMenuEpoxyController
    private lateinit var filterMenuOptionItemEpoxyController: FilterMenuOptionItemEpoxyController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config()

        filterMenuEpoxyController = FilterMenuEpoxyController(viewModel::setSelectedFilterMenuOption)
        filterMenuOptionItemEpoxyController = FilterMenuOptionItemEpoxyController(viewModel::setSelectedFilterMenuOptionItem)

        with(binding) {
            filterOptionsRecyclerView.itemAnimator = null
            filterOptionsRecyclerView.layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean = false
            }
            filterOptionsRecyclerView.setController(filterMenuEpoxyController)

            filterOptionValuesRecyclerView.itemAnimator = null
            filterOptionValuesRecyclerView.setController(filterMenuOptionItemEpoxyController)

            resetButton.setOnClickListener {
                AlertDialog.Builder(root.context)
                    .setTitle(R.string.Reset_filters)
                    .setMessage(R.string.Reset_filters_dialog_message)
                    .setPositiveButton(R.string.Ok) { p0, p1 -> viewModel.resetAllFilters() }
                    .setNegativeButton(R.string.Cancel, null)
                    .create()
                    .show()
            }
        }

        lifecycleScope.launch {
            viewModel.filterOptionsUiStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { result ->
                    result.onSuccess { filterUiState ->
                        filterMenuEpoxyController.setData(filterUiState)

                        val selectedFilterMenuOption = filterUiState.filterMenusUiState
                            .find { it.type == filterUiState.selectedFilterMenuOption }
                        filterMenuOptionItemEpoxyController.setData(selectedFilterMenuOption?.items)
                    }

                }
        }


    }

    private fun config() {
        val modalBottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.skipCollapsed = true
        modalBottomSheetBehavior.isDraggable = false
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.resetFilterMenuOptionSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
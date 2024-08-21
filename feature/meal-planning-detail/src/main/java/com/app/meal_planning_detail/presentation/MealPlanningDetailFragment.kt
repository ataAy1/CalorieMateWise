package com.app.meal_planning_detail.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.search_interactive.databinding.FragmentMealPlanningDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealPlanningDetailFragment : Fragment() {

    private var _binding: FragmentMealPlanningDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MealPlanningDetailFragmentArgs by navArgs()
    private val viewModel: MealPlanningDetailViewModel by viewModels()
    private lateinit var adapter: MealPlanningDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealPlanningDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter = MealPlanningDetailAdapter()

        binding.recyclerViewMealPlans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MealPlanningDetailFragment.adapter
        }

        val date = args.date
        val mealSetID = args.mealSetId

        if (date.isNullOrEmpty() || mealSetID.isNullOrEmpty()) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mealPlanDetailState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.recyclerViewMealPlans.visibility = if (state.isLoading) View.GONE else View.VISIBLE

                state.error?.let {
                }

                if (state.mealPlanDetail.isNotEmpty()) {
                    adapter.submitList(state.mealPlanDetail)
                }
            }
        }

        viewModel.loadMealPlans(date, mealSetID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

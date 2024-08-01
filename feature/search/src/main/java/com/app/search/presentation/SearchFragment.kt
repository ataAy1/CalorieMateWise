package com.app.search.presentation

import FoodAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.search.R
import com.app.search.data.model.ParsedFood
import com.app.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchViewModel.uiState.collect { state ->
                if (state.isLoading) {
                    Log.d("SearchFragment", "Loading data...")
                } else {
                    Log.d("SearchFragment", "Data loading complete.")
                    state.combinedResponse?.let { combinedResponse ->
                        Log.d("SearchFragment", "Data received: $combinedResponse")
                        adapter.submitList(combinedResponse)
                    }
                    state.error?.let { error ->
                        Log.e("SearchFragment", "Error occurred: $error")
                    }
                }
            }
        }

        searchViewModel.search("egg")
    }

    private fun setupRecyclerView() {
        adapter = FoodAdapter()
        binding.searchRecylerview.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecylerview.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

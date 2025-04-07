package com.example.devotio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.devotio.R
import com.example.devotio.data.Temple
import com.example.devotio.ui.dashboard.TempleViewModel
import com.example.devotio.databinding.FragmentTempleDetailBinding

class TempleDetailFragment : Fragment() {
    private var _binding: FragmentTempleDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TempleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTempleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.selectedTemple.observe(viewLifecycleOwner) { temple ->
            temple?.let {
                updateUI(it)
            }
        }
    }

    private fun updateUI(temple: Temple) {
        binding.templeName.text = temple.name
        binding.templeAddress.text = temple.address
        binding.templeDescription.text = temple.description
        binding.templeDeity.text = temple.deity
        binding.templeTiming.text = temple.timing
        
        // Load temple image using Glide or Picasso
        // TODO: Implement image loading
        
        binding.favoriteButton.isSelected = temple.isFavorite
        binding.favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
} 
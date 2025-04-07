package com.example.devotio.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.devotio.R
import com.example.devotio.databinding.FragmentTempleDetailBinding
import com.example.devotio.viewmodel.TempleViewModel

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
        val templeId = arguments?.getInt("templeId", -1) ?: -1
        if (templeId != -1) {
            viewModel.selectTemple(templeId)
        }
        setupToolbar()
        setupClickListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            favoriteButton.setOnClickListener {
                viewModel.selectedTemple.value?.let { temple ->
                    viewModel.toggleFavorite(temple.id)
                }
            }

            directionsButton.setOnClickListener {
                viewModel.selectedTemple.value?.let { temple ->
                    val uri = "geo:${temple.latitude},${temple.longitude}?q=${Uri.encode(temple.name)}"
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedTemple.observe(viewLifecycleOwner) { temple ->
            temple?.let {
                binding.apply {
                    templeName.text = it.name
                    templeAddress.text = it.address
                    templeDeity.text = it.deity
                    templeTiming.text = it.timing
                    templeDescription.text = it.description
                    favoriteButton.isSelected = it.isFavorite

                    // Load temple image using Glide
                    it.imageUrl?.let { url ->
                        Glide.with(templeImage)
                            .load(url)
                            .placeholder(R.drawable.ic_temple_black_24dp)
                            .error(R.drawable.ic_temple_black_24dp)
                            .centerCrop()
                            .into(templeImage)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
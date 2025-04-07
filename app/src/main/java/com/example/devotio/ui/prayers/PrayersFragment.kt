package com.example.devotio.ui.prayers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devotio.R
import com.example.devotio.databinding.FragmentPrayersBinding
import com.google.android.material.snackbar.Snackbar

class PrayersFragment : Fragment() {
    private var _binding: FragmentPrayersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrayersViewModel by viewModels()
    private lateinit var favoritePrayerAdapter: FavoritePrayerAdapter
    private lateinit var prayerCategoryAdapter: PrayerCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initializeAdapters() {
        favoritePrayerAdapter = FavoritePrayerAdapter(
            onPrayerClick = { prayer -> viewModel.playDailyPrayer() },
            onRemoveClick = { prayer -> viewModel.toggleFavorite(prayer) }
        )

        prayerCategoryAdapter = PrayerCategoryAdapter(
            onCategoryClick = { category -> viewModel.navigateToCategory(category.id) }
        )
    }

    private fun setupRecyclerViews() {
        // Setup categories RecyclerView
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = prayerCategoryAdapter
        }

        // Setup favorite prayers RecyclerView
        binding.favoritePrayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritePrayerAdapter
        }
    }

    private fun setupObservers() {
        viewModel.prayerCategories.observe(viewLifecycleOwner) { categories ->
            prayerCategoryAdapter.submitList(categories)
        }

        viewModel.favoritePrayers.observe(viewLifecycleOwner) { prayers ->
            favoritePrayerAdapter.submitList(prayers)
        }

        viewModel.dailyPrayer.observe(viewLifecycleOwner) { prayer ->
            binding.dailyPrayerTitle.text = prayer.title
            binding.dailyPrayerDescription.text = prayer.description
        }

        viewModel.currentPrayer.observe(viewLifecycleOwner) { prayer ->
            binding.playerPrayerTitle.text = prayer.title
            binding.prayerProgressSlider.value = 0f
            binding.playerContainer.visibility = View.VISIBLE
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playerPlayPauseButton.setIconResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            binding.prayerProgressSlider.value = position.toFloat()
            binding.currentTimeText.text = formatDuration(position)
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.prayerProgressSlider.valueTo = duration.toFloat()
            binding.totalTimeText.text = formatDuration(duration)
        }
    }

    private fun setupClickListeners() {
        binding.playDailyPrayerButton.setOnClickListener {
            viewModel.playDailyPrayer()
        }

        binding.playerPlayPauseButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.previousButton.setOnClickListener {
            viewModel.playPreviousPrayer()
        }

        binding.nextButton.setOnClickListener {
            viewModel.playNextPrayer()
        }

        binding.prayerProgressSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.seekTo(value.toInt())
            }
        }

        binding.playerContainer.setOnClickListener {
            // Navigate to full-screen player
            findNavController().navigate(R.id.action_prayersFragment_to_playerFragment)
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
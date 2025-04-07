package com.example.devotio.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devotio.R
import com.example.devotio.data.Prayer
import com.example.devotio.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var categoryAdapter: PrayerCategoryAdapter
    private lateinit var favoritePrayerAdapter: FavoritePrayerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        setupRecyclerViews()
        setupBottomSheet()
        setupClickListeners()
        setupObservers()
        return root
    }

    private fun setupRecyclerViews() {
        // Setup categories RecyclerView
        categoryAdapter = PrayerCategoryAdapter { category ->
            // TODO: Navigate to category prayers
        }
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Setup favorite prayers RecyclerView
        favoritePrayerAdapter = FavoritePrayerAdapter(
            onPrayerClick = { prayer ->
                showPrayerPlayer(prayer)
            },
            onRemoveClick = { prayer ->
                homeViewModel.toggleFavorite(prayer)
            }
        )
        binding.favoritePrayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritePrayerAdapter
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.prayerPlayerContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupClickListeners() {
        binding.playPrayerButton.setOnClickListener {
            homeViewModel.dailyPrayer.value?.let { prayer ->
                showPrayerPlayer(prayer)
            }
        }

        binding.prayerPlayer.playPauseButton.setOnClickListener {
            homeViewModel.togglePlayPause()
        }

        binding.prayerPlayer.previousButton.setOnClickListener {
            // TODO: Play previous prayer
        }

        binding.prayerPlayer.nextButton.setOnClickListener {
            // TODO: Play next prayer
        }
    }

    private fun setupObservers() {
        homeViewModel.dailyPrayer.observe(viewLifecycleOwner) { prayer ->
            binding.dailyPrayerTitle.text = prayer.title
        }

        homeViewModel.prayerCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        homeViewModel.favoritePrayers.observe(viewLifecycleOwner) { prayers ->
            favoritePrayerAdapter.submitList(prayers)
        }

        homeViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.prayerPlayer.playPauseButton.let { button ->
                (button as? MaterialButton)?.setIconResource(
                    if (isPlaying) R.drawable.ic_pause_black_24dp
                    else R.drawable.ic_play_arrow_black_24dp
                )
            }
        }

        binding.prayerPlayer.currentPrayerTitle.text = "Current Prayer"
        binding.prayerPlayer.prayerLyrics.text = "Prayer lyrics will appear here"
    }

    private fun showPrayerPlayer(prayer: Prayer) {
        binding.prayerPlayer.currentPrayerTitle.text = prayer.title
        binding.prayerPlayer.prayerLyrics.text = prayer.description
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
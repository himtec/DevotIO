package com.example.devotio.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.devotio.databinding.FragmentPlayerBinding
import com.example.devotio.player.PlayerViewModel
import com.example.devotio.ui.prayers.PrayersViewModel
import com.example.devotio.models.Prayer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val prayersViewModel: PrayersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClickListeners()
        observePlayerState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.playPauseButton.setOnClickListener {
            playerViewModel.togglePlayPause()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playerViewModel.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun observePlayerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.currentPrayer.collectLatest { prayer: Prayer? ->
                updatePrayerInfo(prayer)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.isPlaying.collectLatest { isPlaying: Boolean ->
                updatePlayPauseButton(isPlaying)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.currentPosition.collectLatest { position: Int ->
                updateSeekBar(position)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.duration.collectLatest { duration: Int ->
                updateDuration(duration)
            }
        }
    }

    private fun updatePrayerInfo(prayer: Prayer?) {
        prayer?.let {
            binding.titleTextView.text = it.title
            binding.descriptionTextView.text = it.description
            // Update other prayer info as needed
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        binding.playPauseButton.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun updateSeekBar(position: Int) {
        binding.seekBar.progress = position
    }

    private fun updateDuration(duration: Int) {
        binding.seekBar.max = duration
        binding.currentTimeTextView.text = formatDuration(duration)
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
package com.example.devotio.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devotio.R
import com.example.devotio.databinding.FragmentPlaylistBinding
import com.example.devotio.ui.prayers.PrayersViewModel
import com.example.devotio.ui.prayers.RepeatMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrayersViewModel by activityViewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(
            onItemClick = { position ->
                viewModel.playPrayer(position)
            }
        )

        binding.playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
        }
    }

    private fun setupClickListeners() {
        binding.shuffleButton.setOnClickListener {
            viewModel.setShuffleEnabled(!viewModel.isShuffleEnabled.value)
        }

        binding.repeatButton.setOnClickListener {
            viewModel.setRepeatMode(
                when (viewModel.repeatMode.value) {
                    RepeatMode.NONE -> RepeatMode.ALL
                    RepeatMode.ALL -> RepeatMode.ONE
                    RepeatMode.ONE -> RepeatMode.NONE
                }
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlist.collectLatest { playlist ->
                playlistAdapter.submitList(playlist)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentIndex.collectLatest { index ->
                playlistAdapter.setCurrentPlayingIndex(index)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isShuffleEnabled.collectLatest { isEnabled ->
                binding.shuffleButton.isSelected = isEnabled
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.repeatMode.collectLatest { mode ->
                updateRepeatButtonIcon(mode)
            }
        }
    }

    private fun updateRepeatButtonIcon(mode: RepeatMode) {
        binding.repeatButton.setImageResource(
            when (mode) {
                RepeatMode.NONE -> R.drawable.ic_repeat
                RepeatMode.ALL -> R.drawable.ic_repeat_all
                RepeatMode.ONE -> R.drawable.ic_repeat_one
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
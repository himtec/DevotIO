package com.example.devotio.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.devotio.databinding.FragmentAudioEffectsBinding
import com.example.devotio.player.AudioEffectsManager
import com.example.devotio.player.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AudioEffectsFragment : Fragment() {
    private var _binding: FragmentAudioEffectsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by activityViewModels()
    private lateinit var effectsManager: AudioEffectsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioEffectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupEffectsManager()
        setupClickListeners()
        observeEffects()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupEffectsManager() {
        effectsManager = viewModel.getAudioEffectsManager()
    }

    private fun setupClickListeners() {
        binding.playbackSpeedSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                effectsManager.setPlaybackSpeed(value)
                binding.playbackSpeedValue.text = String.format("%.1fx", value)
            }
        }

        binding.bassBoostSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                effectsManager.setBassBoost(value.toInt().toShort())
                binding.bassBoostValue.text = "${value.toInt()}%"
            }
        }

        binding.trebleBoostSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                effectsManager.setTrebleBoost(value.toInt().toShort())
                binding.trebleBoostValue.text = "${value.toInt()}%"
            }
        }

        binding.reverbSwitch.setOnCheckedChangeListener { _, isChecked ->
            effectsManager.setReverbEnabled(isChecked)
        }

        binding.loudnessEnhancementSwitch.setOnCheckedChangeListener { _, isChecked ->
            effectsManager.setLoudnessEnhancement(isChecked)
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            effectsManager.playbackSpeed.collectLatest { speed ->
                binding.playbackSpeedSlider.value = speed
                binding.playbackSpeedValue.text = String.format("%.1fx", speed)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            effectsManager.bassBoost.collectLatest { level ->
                binding.bassBoostSlider.value = level.toFloat()
                binding.bassBoostValue.text = "$level%"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            effectsManager.trebleBoost.collectLatest { level ->
                binding.trebleBoostSlider.value = level.toFloat()
                binding.trebleBoostValue.text = "$level%"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            effectsManager.reverbEnabled.collectLatest { enabled ->
                binding.reverbSwitch.isChecked = enabled
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
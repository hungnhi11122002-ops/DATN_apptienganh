package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.EnglishWithStork.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDictionaryBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
    }

    private fun setupEvents() {

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSpeak.setOnClickListener {

        }

        binding.btnSpeakSmall.setOnClickListener {

        }


        binding.btnSave.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
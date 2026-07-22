package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.SavedVocabularyEntity
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.SessionManager
import com.example.EnglishWithStork.UI.VocabularyAdapter
import com.example.EnglishWithStork.databinding.FragmentSoTayBinding
import com.example.EnglishWithStork.util.EnglishTtsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SoTay : Fragment() {

    private var _binding:
            FragmentSoTayBinding? = null

    private val binding:
            FragmentSoTayBinding
        get() = _binding!!

    private lateinit var database:
            AppDatabase

    private lateinit var vocabularyAdapter:
            VocabularyAdapter

    private lateinit var ttsManager:
            EnglishTtsManager

    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSoTayBinding.inflate(
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
        super.onViewCreated(
            view,
            savedInstanceState
        )

        database =
            AppDatabase.getDatabase(
                requireContext()
            )

        userId =
            SessionManager(
                requireContext()
            ).getUserId()

        ttsManager =
            EnglishTtsManager(
                requireContext()
            )

        setupRecyclerView()
        observeSavedVocabularies()
    }

    private fun setupRecyclerView() {

        vocabularyAdapter =
            VocabularyAdapter(
                showTopic = false,
                onSpeakClick = { vocabulary ->
                    speakVocabulary(vocabulary)
                },

                onSaveClick = {
                        vocabulary,
                        isSaved ->

                    toggleSavedVocabulary(
                        vocabulary = vocabulary,
                        isSaved = isSaved
                    )
                }
            )

        binding.rvSavedVocabulary.apply {

            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )

            adapter =
                vocabularyAdapter

            setHasFixedSize(true)
        }
    }

    private fun speakVocabulary(
        vocabulary: VocabularyEntity
    ) {
        val success =
            ttsManager.speak(
                vocabulary.english
            )

        if (!success) {
            Toast.makeText(
                requireContext(),
                "Máy đọc chưa sẵn sàng hoặc chưa hỗ trợ tiếng Anh",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleSavedVocabulary(
        vocabulary: VocabularyEntity,
        isSaved: Boolean
    ) {
        if (userId <= 0) {
            Toast.makeText(
                requireContext(),
                "Không tìm thấy tài khoản đang đăng nhập",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            if (isSaved) {
                database
                    .savedVocabularyDao()
                    .deleteSavedVocabulary(
                        userId = userId,
                        vocabularyId = vocabulary.id
                    )

                Toast.makeText(
                    requireContext(),
                    "Đã xóa khỏi sổ tay",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                database
                    .savedVocabularyDao()
                    .insertSavedVocabulary(
                        SavedVocabularyEntity(
                            userId = userId,
                            vocabularyId = vocabulary.id
                        )
                    )
            }
        }
    }

    private fun observeSavedVocabularies() {

        if (userId <= 0) {
            binding.tvEmpty.text =
                "Vui lòng đăng nhập để sử dụng sổ tay"

            binding.tvEmpty.isVisible = true
            binding.rvSavedVocabulary.isVisible = false
            binding.tvSavedCount.text = "0 từ đã lưu"

            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                database
                    .savedVocabularyDao()
                    .observeSavedVocabularies(
                        userId
                    )
                    .collectLatest { vocabularies ->

                        vocabularyAdapter.submitList(
                            vocabularies
                        )

                        vocabularyAdapter
                            .setSavedVocabularyIds(
                                vocabularies
                                    .map { it.id }
                                    .toSet()
                            )

                        binding.tvSavedCount.text =
                            "${vocabularies.size} từ đã lưu"

                        val isEmpty =
                            vocabularies.isEmpty()

                        binding.tvEmpty.isVisible =
                            isEmpty

                        binding.rvSavedVocabulary.isVisible =
                            !isEmpty
                    }
            }
        }
    }

    override fun onDestroyView() {

        if (::ttsManager.isInitialized) {
            ttsManager.release()
        }

        binding.rvSavedVocabulary.adapter = null
        _binding = null

        super.onDestroyView()
    }
}
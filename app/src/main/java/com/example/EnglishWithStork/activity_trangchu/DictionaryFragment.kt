package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.SavedVocabularyEntity
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.SessionManager
import com.example.EnglishWithStork.databinding.FragmentDictionaryBinding
import com.example.EnglishWithStork.util.EnglishTtsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

    private val binding: FragmentDictionaryBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    private lateinit var ttsManager: EnglishTtsManager

    private var userId: Int = -1

    private var currentVocabulary: VocabularyEntity? = null

    private var savedVocabularyIds: Set<Int> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentDictionaryBinding.inflate(
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

        setupEvents()
        observeSavedVocabularyIds()
    }

    private fun setupEvents() {

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Bấm icon kính lúp
        binding.imgSearch.setOnClickListener {
            searchWord()
        }

        // Bấm nút Search trên bàn phím
        binding.etSearchWord
            .setOnEditorActionListener {
                    _,
                    actionId,
                    _ ->

                if (
                    actionId ==
                    EditorInfo.IME_ACTION_SEARCH
                ) {

                    searchWord()

                    true
                } else {
                    false
                }
            }

        binding.btnSpeak.setOnClickListener {
            speakCurrentWord()
        }

        binding.btnSpeakSmall.setOnClickListener {
            speakCurrentWord()
        }

        binding.btnSave.setOnClickListener {
            toggleSavedVocabulary()
        }
    }

    private fun searchWord() {

        val keyword =
            binding.etSearchWord
                .text
                .toString()
                .trim()

        if (keyword.isBlank()) {

            binding.etSearchWord.error =
                "Vui lòng nhập từ cần tra"

            return
        }

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                val vocabulary =
                    database
                        .vocabularyDao()
                        .findExactEnglishWord(
                            keyword
                        )

                currentVocabulary =
                    vocabulary

                if (vocabulary == null) {

                    binding.layoutResult.isVisible =
                        false

                    Toast.makeText(
                        requireContext(),
                        "Không tìm thấy \"$keyword\" trong dữ liệu hiện tại",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                showVocabulary(
                    vocabulary
                )
            }
    }

    private fun showVocabulary(
        vocabulary: VocabularyEntity
    ) {

        binding.layoutResult.isVisible =
            true

        binding.tvWord.text =
            vocabulary.english

        binding.tvPhonetic.text =
            vocabulary.phonetic.orEmpty()

        binding.tvPhonetic.isVisible =
            !vocabulary.phonetic
                .isNullOrBlank()

        binding.tvPartOfSpeech.text =
            vocabulary.wordClass.orEmpty()

        binding.tvPartOfSpeech.isVisible =
            !vocabulary.wordClass
                .isNullOrBlank()

        binding.tvMeaning.text =
            vocabulary.vietnamese

        binding.tvDefinition.text =
            "Chưa có định nghĩa tiếng Anh trong dữ liệu offline."

        binding.tvExample.text =
            vocabulary.exampleEnglish
                ?: "Chưa có câu ví dụ."

        updateSaveButton()
    }

    private fun speakCurrentWord() {

        val vocabulary =
            currentVocabulary
                ?: return

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

    private fun toggleSavedVocabulary() {

        val vocabulary =
            currentVocabulary
                ?: return

        if (userId <= 0) {

            Toast.makeText(
                requireContext(),
                "Không tìm thấy tài khoản đang đăng nhập",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val isSaved =
            savedVocabularyIds.contains(
                vocabulary.id
            )

        viewLifecycleOwner
            .lifecycleScope
            .launch {

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
                                vocabularyId =
                                    vocabulary.id
                            )
                        )

                    Toast.makeText(
                        requireContext(),
                        "Đã lưu vào sổ tay",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun observeSavedVocabularyIds() {

        if (userId <= 0) {
            return
        }

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        database
                            .savedVocabularyDao()
                            .observeSavedVocabularyIds(
                                userId
                            )
                            .collectLatest { ids ->

                                savedVocabularyIds =
                                    ids.toSet()

                                updateSaveButton()
                            }
                    }
            }
    }

    private fun updateSaveButton() {

        val vocabulary =
            currentVocabulary
                ?: return

        val isSaved =
            savedVocabularyIds.contains(
                vocabulary.id
            )

        binding.btnSave.text =
            if (isSaved) {
                "Đã lưu"
            } else {
                "Lưu từ"
            }

        binding.btnSave
            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (isSaved) {
                    R.drawable.ic_bookmark_24
                } else {
                    R.drawable.ic_bookmark_border_24
                },
                0,
                0,
                0
            )
    }

    override fun onDestroyView() {

        if (::ttsManager.isInitialized) {
            ttsManager.release()
        }

        _binding = null

        super.onDestroyView()
    }
}
package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.EnglishWithStork.Models.DictionaryLookupResult
import com.example.EnglishWithStork.Models.DictionaryLookupState
import com.example.EnglishWithStork.repository.DictionaryRepository

class DictionaryFragment : Fragment() {
    private var _binding: FragmentDictionaryBinding? = null
    private val binding: FragmentDictionaryBinding
        get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var ttsManager: EnglishTtsManager
    private var userId: Int = -1
    private lateinit var dictionaryRepository: DictionaryRepository

    private var currentResult: DictionaryLookupResult? = null
    private var savedVocabularyIds: Set<Int> = emptySet()
    private var searchJob: Job? = null

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

        dictionaryRepository =
            DictionaryRepository(
                database.vocabularyDao()
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

        setupSearch()

        observeSavedVocabularyIds()
    }


    /**
     * Xử lý các sự kiện click trên giao diện.
     */
    private fun setupEvents() {

        binding.btnBack.setOnClickListener {

            parentFragmentManager
                .popBackStack()
        }


        /**
         * User bấm icon tìm kiếm.
         */
        binding.imgSearch.setOnClickListener {

            searchFromInput()
        }


        /**
         * User bấm Search trên bàn phím.
         */
        binding.etSearchWord
            .setOnEditorActionListener {
                    _,
                    actionId,
                    _ ->

                if (
                    actionId ==
                    EditorInfo.IME_ACTION_SEARCH
                ) {

                    searchFromInput()

                    true

                } else {

                    false
                }
            }


        binding.btnSpeak
            .setOnClickListener {

                speakCurrentWord()
            }


        binding.btnSpeakSmall
            .setOnClickListener {

                speakCurrentWord()
            }


        binding.btnSave
            .setOnClickListener {

                toggleSavedVocabulary()
            }
    }


    /**
     * Tìm tự động khi user đang nhập.
     *
     * Có delay 300ms để tránh query Room liên tục
     * cho từng ký tự.
     */
    private fun setupSearch() {

        binding.etSearchWord
            .doAfterTextChanged { text ->

                val keyword =
                    text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                /**
                 * Xóa error cũ khi user nhập lại.
                 */
                if (keyword.isNotEmpty()) {

                    binding.etSearchWord.error =
                        null
                }

                searchVocabulary(
                    keyword = keyword,
                    useDelay = true,
                    allowOnline = false
                )
            }
    }


    /**
     * Được gọi khi user chủ động bấm:
     *
     * - icon Search
     * - Search trên bàn phím
     *
     * Không cần delay.
     */
    private fun searchFromInput() {

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

        binding.etSearchWord.error =
            null

        searchVocabulary(
            keyword = keyword,
            useDelay = false,
            allowOnline = true
        )
    }


    /**
     * Hàm tìm kiếm DUY NHẤT.
     *
     * Tất cả thao tác tìm kiếm đều đi qua hàm này.
     */
    private fun searchVocabulary(

        keyword: String,

        useDelay: Boolean,

        allowOnline: Boolean

    ) {

        searchJob?.cancel()


        if (keyword.isBlank()) {

            currentResult = null

            binding.layoutResult.isVisible =
                false

            binding.btnSave.isVisible =
                false

            binding.tvResultTitle.text =
                "Kết quả tra cứu"

            return
        }


        searchJob =
            viewLifecycleOwner
                .lifecycleScope
                .launch {

                    if (useDelay) {

                        delay(300)
                    }


                    when (
                        val state =
                            dictionaryRepository
                                .lookup(
                                    keyword = keyword,
                                    allowOnline = allowOnline
                                )
                    ) {


                        is DictionaryLookupState.Found -> {

                            showDictionaryResult(
                                state.result
                            )
                        }


                        DictionaryLookupState.OfflineMiss -> {

                            currentResult = null

                            binding.layoutResult.isVisible =
                                false

                            binding.btnSave.isVisible =
                                false

                            binding.tvResultTitle.text =
                                "Không có trong dữ liệu offline. Nhấn tìm kiếm để tra online."
                        }


                        DictionaryLookupState.NotFound -> {

                            showNotFound(keyword)
                        }


                        is DictionaryLookupState.Error -> {

                            showSearchError(
                                state.message
                            )
                        }
                    }
                }
    }

    /**
     * Hiển thị từ tìm được lên giao diện.
     *
     * Chỉ giữ MỘT showVocabulary().
     */
    private fun showDictionaryResult(
        result: DictionaryLookupResult
    ) {

        currentResult = result


        binding.tvResultTitle.text =
            "Kết quả tra cứu"


        binding.layoutResult.isVisible =
            true


        // =============================
        // English
        // =============================

        binding.tvWord.text =
            result.english


        // =============================
        // Phonetic
        // =============================

        binding.tvPhonetic.text =
            result.phonetic.orEmpty()

        binding.tvPhonetic.isVisible =
            !result.phonetic.isNullOrBlank()


        // =============================
        // Part of speech
        // =============================

        binding.tvPartOfSpeech.text =
            result.wordClass.orEmpty()

        binding.tvPartOfSpeech.isVisible =
            !result.wordClass.isNullOrBlank()


        // =============================
        // Vietnamese meaning
        // =============================

        binding.tvMeaning.text =
            result
                .vietnamese
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Chưa có nghĩa tiếng Việt."


        // =============================
        // English definition
        // =============================

        binding.tvDefinition.text =
            result
                .definition
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: if (
                    result.localVocabularyId != null
                ) {

                    "Chưa có định nghĩa tiếng Anh trong dữ liệu offline."

                } else {

                    "Chưa có định nghĩa tiếng Anh."
                }


        // =============================
        // Example
        // =============================

        val exampleText =
            buildString {

                result
                    .exampleEnglish
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?.let {
                        append(it)
                    }


                result
                    .exampleVietnamese
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?.let {

                        if (isNotEmpty()) {

                            append("\n")
                        }

                        append(it)
                    }
            }


        binding.tvExample.text =
            if (exampleText.isBlank()) {

                "Chưa có câu ví dụ."

            } else {

                exampleText
            }


        /**
         * Chỉ từ nằm trong Room mới có thể
         * sử dụng cấu trúc SavedVocabulary hiện tại.
         */
        if (result.localVocabularyId != null) {

            binding.btnSave.isVisible = true

            updateSaveButton()

        } else {

            binding.btnSave.isVisible = false
        }
    }

    /**
     * Không tìm thấy từ trong Room.
     */
    private fun showNotFound(
        keyword: String
    ) {

        currentResult = null

        binding.layoutResult.isVisible =
            false

        binding.btnSave.isVisible =
            false

        binding.tvResultTitle.text =
            "Không tìm thấy từ \"$keyword\""
    }

    private fun showSearchError(
        message: String
    ) {

        currentResult = null

        binding.layoutResult.isVisible =
            false

        binding.btnSave.isVisible =
            false

        binding.tvResultTitle.text =
            message
    }

    /**
     * Phát âm từ đang hiển thị.
     */
    private fun speakCurrentWord() {

        val result =
            currentResult
                ?: return


        val success =
            ttsManager.speak(
                result.english
            )


        if (!success) {

            Toast.makeText(
                requireContext(),
                "Máy đọc chưa sẵn sàng hoặc chưa hỗ trợ tiếng Anh",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Lưu / xóa từ khỏi sổ tay.
     */
    private fun toggleSavedVocabulary() {

        val vocabularyId =
            currentResult
                ?.localVocabularyId


        if (vocabularyId == null) {

            Toast.makeText(
                requireContext(),
                "Từ online hiện chưa hỗ trợ lưu vào sổ tay",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (userId <= 0) {

            Toast.makeText(
                requireContext(),
                "Không tìm thấy tài khoản đang đăng nhập",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val isSaved =
            savedVocabularyIds
                .contains(
                    vocabularyId
                )


        viewLifecycleOwner
            .lifecycleScope
            .launch {

                if (isSaved) {

                    database
                        .savedVocabularyDao()
                        .deleteSavedVocabulary(
                            userId = userId,
                            vocabularyId = vocabularyId
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

                                userId =
                                    userId,

                                vocabularyId =
                                    vocabularyId
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

    /**
     * Theo dõi danh sách ID của các từ
     * user đã lưu vào sổ tay.
     */
    private fun observeSavedVocabularyIds() {
        if (userId <= 0) {return}

        viewLifecycleOwner
            .lifecycleScope
            .launch {
                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {
                        database
                            .savedVocabularyDao()
                            .observeSavedVocabularyIds(userId)
                            .collectLatest { ids ->
                                savedVocabularyIds = ids.toSet()
                                updateSaveButton()
                            }
                    }
            }
    }
    /**
     * Thay đổi nút Save tùy theo
     * từ hiện tại đã được lưu hay chưa.
     */
    private fun updateSaveButton() {

        val vocabularyId =
            currentResult
                ?.localVocabularyId


        if (vocabularyId == null) {

            binding.btnSave.isVisible =
                false

            return
        }


        binding.btnSave.isVisible =
            true


        val isSaved =
            savedVocabularyIds
                .contains(
                    vocabularyId
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
        /**
         * Hủy search đang chờ delay.
         */
        searchJob?.cancel()
        searchJob = null
        if (::ttsManager.isInitialized)
            {ttsManager.release()}
        _binding = null
        super.onDestroyView()
    }
}
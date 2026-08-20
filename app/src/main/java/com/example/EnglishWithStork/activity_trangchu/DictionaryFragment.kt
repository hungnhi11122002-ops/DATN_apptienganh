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

class DictionaryFragment : Fragment() {
    private var _binding: FragmentDictionaryBinding? = null
    private val binding: FragmentDictionaryBinding
        get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var ttsManager: EnglishTtsManager
    private var userId: Int = -1
    private var currentVocabulary: VocabularyEntity? = null
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
                    useDelay = true
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
            useDelay = false
        )
    }


    /**
     * Hàm tìm kiếm DUY NHẤT.
     *
     * Tất cả thao tác tìm kiếm đều đi qua hàm này.
     */
    private fun searchVocabulary(
        keyword: String,
        useDelay: Boolean
    ) {

        /**
         * Hủy lần search trước nếu user nhập tiếp.
         */
        searchJob?.cancel()


        /**
         * User xóa hết nội dung tìm kiếm.
         */
        if (keyword.isBlank()) {
            currentVocabulary = null
            binding.layoutResult.isVisible = false
            binding.tvResultTitle.text = "Kết quả tra cứu"
            return
        }


        searchJob =
            viewLifecycleOwner
                .lifecycleScope
                .launch {

                    /**
                     * Search khi đang gõ:
                     * đợi 300ms.
                     *
                     * Search bằng nút:
                     * tìm ngay.
                     */
                    if (useDelay)
                    {
                        delay(300)
                    }

                    /**
                     * Tìm chính xác từ tiếng Anh
                     * trong Room Database.
                     */
                    val vocabulary =
                        database
                            .vocabularyDao()
                            .findExactEnglishWord(keyword)

                    if (vocabulary != null)
                    {
                        showVocabulary(vocabulary)
                    }
                    else
                    {
                        showNotFound(keyword)
                    }
                }
    }

    /**
     * Hiển thị từ tìm được lên giao diện.
     *
     * Chỉ giữ MỘT showVocabulary().
     */
    private fun showVocabulary(
        vocabulary: VocabularyEntity
    ) {
        /**
         * Lưu từ hiện tại để:
         *
         * - phát âm
         * - lưu vào sổ tay
         */
        currentVocabulary = vocabulary
        binding.tvResultTitle.text = "Kết quả tra cứu"
        binding.layoutResult.isVisible = true

        /**
         * English.
         */
        binding.tvWord.text = vocabulary.english

        /**
         * Phiên âm.
         */
        binding.tvPhonetic.text = vocabulary.phonetic.orEmpty()
        binding.tvPhonetic.isVisible = !vocabulary.phonetic.isNullOrBlank()

        /**
         * Loại từ.
         */
        binding.tvPartOfSpeech.text = vocabulary.wordClass.orEmpty()
        binding.tvPartOfSpeech.isVisible = !vocabulary.wordClass.isNullOrBlank()
        /**
         * Nghĩa tiếng Việt.
         */
        binding.tvMeaning.text = vocabulary.vietnamese
        /**
         * VocabularyEntity hiện tại
         * chưa có trường definition.
         */
        binding.tvDefinition.text = "Chưa có định nghĩa tiếng Anh trong dữ liệu offline."
        /**
         * Ghép ví dụ tiếng Anh
         * và ví dụ tiếng Việt.
         */
        val exampleText =
            buildString {
                vocabulary.exampleEnglish
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?.let {
                        append(it)
                    }

                vocabulary.exampleVietnamese
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?.let {
                        if (isNotEmpty())
                        {
                            append("\n")
                        }
                        append(it)
                    }
            }

        binding.tvExample.text =
            if (exampleText.isBlank())
                {"Chưa có câu ví dụ."}
            else
                {exampleText }
        /**
         * Kiểm tra từ này đã được lưu
         * vào sổ tay chưa.
         */
        updateSaveButton()
    }

    /**
     * Không tìm thấy từ trong Room.
     */
    private fun showNotFound(
        keyword: String
    ) {
        currentVocabulary = null
        binding.layoutResult.isVisible = false
        binding.tvResultTitle.text = "Không tìm thấy từ \"$keyword\""
    }
    /**
     * Phát âm từ đang hiển thị.
     */
    private fun speakCurrentWord() {
        val vocabulary = currentVocabulary ?: return
        val success = ttsManager.speak(vocabulary.english)

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
        val vocabulary = currentVocabulary ?: return
        if (userId <= 0) {
            Toast.makeText(requireContext(),"Không tìm thấy tài khoản đang đăng nhập",Toast.LENGTH_SHORT
            ).show()
            return
        }

        val isSaved = savedVocabularyIds.contains(vocabulary.id)
        viewLifecycleOwner
            .lifecycleScope
            .launch {
                if (isSaved) {
                    /**
                     * Từ đã lưu
                     * -> xóa khỏi sổ tay.
                     */
                    database
                        .savedVocabularyDao()
                        .deleteSavedVocabulary(
                            userId = userId,
                            vocabularyId = vocabulary.id
                        )

                    Toast.makeText(requireContext(),"Đã xóa khỏi sổ tay",Toast.LENGTH_SHORT).show()

                } else {
                    /**
                     * Từ chưa lưu
                     * -> thêm vào sổ tay.
                     */
                    database
                        .savedVocabularyDao()
                        .insertSavedVocabulary(
                            SavedVocabularyEntity(
                                userId = userId,
                                vocabularyId =
                                    vocabulary.id
                            )
                        )
                    Toast.makeText(requireContext(),"Đã lưu vào sổ tay",Toast.LENGTH_SHORT).show()
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
        val vocabulary = currentVocabulary ?: return
        val isSaved = savedVocabularyIds.contains(vocabulary.id)
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
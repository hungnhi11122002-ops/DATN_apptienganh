package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
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
import com.example.EnglishWithStork.databinding.FragmentVocabListBinding
import com.example.EnglishWithStork.util.EnglishTtsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VocabListFragment : Fragment() {

    private var _binding:
            FragmentVocabListBinding? = null

    private val binding:
            FragmentVocabListBinding
        get() = _binding!!

    private lateinit var vocabularyAdapter:
            VocabularyAdapter

    private lateinit var database:
            AppDatabase

    private lateinit var sessionManager:
            SessionManager

    private lateinit var ttsManager:
            EnglishTtsManager

    private var topicId: Int = -1
    private var topicName: String = ""
    private var userId: Int = -1

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        topicId = requireArguments().getInt(
            ARG_TOPIC_ID,
            -1
        )

        topicName = requireArguments()
            .getString(ARG_TOPIC_NAME)
            .orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentVocabListBinding.inflate(
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

        sessionManager =
            SessionManager(
                requireContext()
            )

        userId =
            sessionManager.getUserId()

        ttsManager =
            EnglishTtsManager(
                requireContext()
            )

        setupHeader()
        setupRecyclerView()
        observeData()
    }

    private fun setupHeader() {
        binding.tvTopicTitle.text =
            topicName

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {

        vocabularyAdapter =
            VocabularyAdapter(

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

        binding.rvVocabulary.apply {

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

                Toast.makeText(
                    requireContext(),
                    "Đã lưu vào sổ tay",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeData() {
        if (topicId <= 0) {
            binding.tvEmpty.text =
                "Không tìm thấy mã chủ đề"

            binding.tvEmpty.isVisible = true
            binding.rvVocabulary.isVisible = false

            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                /**
                 * Luồng 1:
                 * Theo dõi danh sách từ của chủ đề.
                 */
                launch {
                    database
                        .vocabularyDao()
                        .observeWordsByTopic(topicId)
                        .collectLatest { vocabularies ->

                            vocabularyAdapter.submitList(
                                vocabularies
                            )

                            binding.tvWordCount.text =
                                "${vocabularies.size} từ"

                            val isEmpty =
                                vocabularies.isEmpty()

                            binding.tvEmpty.isVisible =
                                isEmpty

                            binding.rvVocabulary.isVisible =
                                !isEmpty
                        }
                }

                /**
                 * Luồng 2:
                 * Theo dõi ID các từ đã lưu để đổi icon.
                 */
                if (userId > 0) {
                    launch {
                        database
                            .savedVocabularyDao()
                            .observeSavedVocabularyIds(
                                userId
                            )
                            .collectLatest { savedIds ->

                                vocabularyAdapter
                                    .setSavedVocabularyIds(
                                        savedIds.toSet()
                                    )
                            }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {

        if (::ttsManager.isInitialized) {
            ttsManager.release()
        }

        binding.rvVocabulary.adapter = null
        _binding = null

        super.onDestroyView()
    }

    companion object {

        private const val ARG_TOPIC_ID =
            "arg_topic_id"

        private const val ARG_TOPIC_NAME =
            "arg_topic_name"

        fun newInstance(
            topicId: Int,
            topicName: String
        ): VocabListFragment {

            return VocabListFragment().apply {

                arguments = bundleOf(
                    ARG_TOPIC_ID to topicId,
                    ARG_TOPIC_NAME to topicName
                )
            }
        }
    }
}
package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.UI.VocabularyAdapter
import com.example.EnglishWithStork.databinding.FragmentVocabListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VocabListFragment : Fragment() {

    private var _binding: FragmentVocabListBinding? = null

    private val binding: FragmentVocabListBinding
        get() = _binding!!

    private lateinit var vocabularyAdapter: VocabularyAdapter

    private var topicId: Int = -1
    private var topicName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicId = requireArguments().getInt(
            ARG_TOPIC_ID,
            -1
        )

        topicName = requireArguments().getString(
            ARG_TOPIC_NAME
        ).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVocabListBinding.inflate(
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

        setupHeader()
        setupRecyclerView()
        observeVocabularies()
    }

    private fun setupHeader() {

        binding.tvTopicTitle.text = topicName

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {

        vocabularyAdapter = VocabularyAdapter()

        binding.rvVocabulary.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            adapter = vocabularyAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeVocabularies() {

        if (topicId <= 0) {
            binding.tvEmpty.text =
                "Không tìm thấy mã chủ đề"

            binding.tvEmpty.isVisible = true
            binding.rvVocabulary.isVisible = false
            return
        }

        val database = AppDatabase.getDatabase(
            requireContext()
        )

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

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
        }
    }

    override fun onDestroyView() {

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
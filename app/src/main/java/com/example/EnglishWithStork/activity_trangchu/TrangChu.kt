package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.Models.quick_practise
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.UI.PractiseAdapter
import com.example.EnglishWithStork.UI.TopicAdapter
import com.example.EnglishWithStork.databinding.FragmentTrangChuBinding

class TrangChu : Fragment() {

    private var _binding: FragmentTrangChuBinding? = null

    private val binding: FragmentTrangChuBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTrangChuBinding.inflate(
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

        setupTopics()
        setupQuickPractise()
    }


    // ==============================
    // DANH SÁCH CHỦ ĐỀ TRANG CHỦ
    // ==============================

    private fun setupTopics() {

        val listTopic = listOf(

            Topic(
                topic_name = "Gia đình",
                description = "25 từ",
                image_description = R.drawable.family,
                isCompleted = false,
                topic_id = 2
            ),

            Topic(
                topic_name = "Nghề nghiệp",
                description = "25 từ",
                image_description = R.drawable.jobs,
                isCompleted = false,
                topic_id = 3
            ),

            Topic(
                topic_name = "Trái cây",
                description = "25 từ",
                image_description = R.drawable.fruits,
                isCompleted = false,
                topic_id = 10
            ),

            Topic(
                topic_name = "Động vật",
                description = "25 từ",
                image_description = R.drawable.animals,
                isCompleted = false,
                topic_id = 12
            )
        )


        binding.rvItemTopic.apply {

            layoutManager = GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.HORIZONTAL,
                false
            )

            adapter = TopicAdapter(
                listTopic
            ) { selectedTopic ->

                openVocabularyList(
                    selectedTopic
                )
            }

            setHasFixedSize(true)
        }
    }


    // ==============================
    // ÔN TẬP / KIỂM TRA NHANH
    // ==============================

    private fun setupQuickPractise() {

        val listQuickPractise = listOf(

            quick_practise(
                "Ôn tập",
                "Ôn tập lại từ vựng",
                R.drawable.ic_ontap
            ),

            quick_practise(
                "Kiểm tra",
                "Kiểm tra kiến thức",
                R.drawable.ic_exam
            )
        )


        binding.rvItemLuyentap.apply {

            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = PractiseAdapter(
                listQuickPractise
            ) { selectedPractise ->

                when (selectedPractise.name) {

                    "Ôn tập" -> {

                        openPracticeTopics(
                            OnTapFragment.MODE_REVIEW
                        )
                    }

                    "Kiểm tra" -> {

                        openPracticeTopics(
                            OnTapFragment.MODE_TEST
                        )
                    }
                }
            }

            setHasFixedSize(true)
        }
    }


    // ==============================
    // MỞ DANH SÁCH TỪ VỰNG
    // ==============================

    private fun openVocabularyList(
        topic: Topic
    ) {

        if (topic.topic_id <= 0) {

            Toast.makeText(
                requireContext(),
                "Chủ đề này chưa có dữ liệu từ vựng",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val vocabListFragment =
            VocabListFragment.newInstance(
                topicId = topic.topic_id,
                topicName = topic.topic_name
            )


        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frame_layout,
                vocabListFragment
            )
            .addToBackStack(
                "vocab_list"
            )
            .commit()
    }


    // ==============================
    // MỞ ÔN TẬP / KIỂM TRA
    // ==============================

    private fun openPracticeTopics(
        mode: String
    ) {

        val fragment = ReviewTopicFragment.newInstance(mode)


        val backStackName =
            if (
                mode == OnTapFragment.MODE_TEST
            ) {

                "test_topics"

            } else {

                "review_topics"
            }


        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frame_layout,
                fragment
            )
            .addToBackStack(
                backStackName
            )
            .commit()
    }


    override fun onDestroyView() {

        binding.rvItemTopic.adapter = null
        binding.rvItemLuyentap.adapter = null

        _binding = null

        super.onDestroyView()
    }
}
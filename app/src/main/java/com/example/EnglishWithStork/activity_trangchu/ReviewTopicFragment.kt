package com.example.EnglishWithStork.activity_trangchu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.UI.TopicAdapter
import com.example.EnglishWithStork.activity_dangnhap
import com.example.EnglishWithStork.databinding.FragmentReviewTopicBinding

class ReviewTopicFragment : Fragment() {

    private var _binding: FragmentReviewTopicBinding? = null

    private val binding: FragmentReviewTopicBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReviewTopicBinding.inflate(
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

        setupTopics()
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupTopics() {

        val listTopics = listOf(

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
                topic_name = "Hoa quả",
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
            ),

            Topic(
                topic_name = "Số đếm và phép tính",
                description = "5 từ",
                image_description = R.drawable.ic_tinhtoan_sodem,
                isCompleted = false,
                topic_id = 1
            ),

            Topic(
                topic_name = "Thời tiết",
                description = "5 từ",
                image_description = R.drawable.ic_thoitiet1,
                isCompleted = false,
                topic_id = 4
            ),

            Topic(
                topic_name = "Quần áo",
                description = "5 từ",
                image_description = R.drawable.ic_clothes,
                isCompleted = false,
                topic_id = 5
            ),

            Topic(
                topic_name = "Tính cách",
                description = "5 từ",
                image_description = R.drawable.ic_tinhcach,
                isCompleted = false,
                topic_id = 6
            ),

            Topic(
                topic_name = "Bộ phận cơ thể",
                description = "5 từ",
                image_description = R.drawable.ic_bophancothe,
                isCompleted = false,
                topic_id = 7
            ),

            Topic(
                topic_name = "Ngoại hình",
                description = "5 từ",
                image_description = R.drawable.ic_ngoaihinh,
                isCompleted = false,
                topic_id = 8
            ),

            Topic(
                topic_name = "Cảm xúc",
                description = "5 từ",
                image_description = R.drawable.ic_camxuc,
                isCompleted = false,
                topic_id = 9
            ),

            Topic(
                topic_name = "Rau củ",
                description = "5 từ",
                image_description = R.drawable.ic_raucu,
                isCompleted = false,
                topic_id = 11
            ),

            Topic(
                topic_name = "Đồ ăn & Đồ uống",
                description = "5 từ",
                image_description = R.drawable.ic_doan_douong,
                isCompleted = false,
                topic_id = 13
            ),

            Topic(
                topic_name = "Quốc gia",
                description = "5 từ",
                image_description = R.drawable.ic_quocgia,
                isCompleted = false,
                topic_id = 14
            ),

            Topic(
                topic_name = "Mua sắm",
                description = "5 từ",
                image_description = R.drawable.ic_muasam,
                isCompleted = false,
                topic_id = 15
            ),

            Topic(
                topic_name = "Sức khỏe",
                description = "5 từ",
                image_description = R.drawable.ic_suckhoe,
                isCompleted = false,
                topic_id = 16
            )
        )

        binding.rvTopic.apply {

            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            adapter = TopicAdapter(
                listTopic = listTopics,
                fullWidth = true
            ) { topic ->

                openReview(topic)
            }

            setHasFixedSize(true)
        }
    }

    private fun openReview(topic: Topic) {

        if (topic.topic_id <= 0) {
            return
        }

        val fragment = OnTapFragment.newInstance(
            topicId = topic.topic_id,
            topicName = topic.topic_name
        )

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frame_layout,
                fragment
            )
            .addToBackStack("review_quiz")
            .commit()
    }

    override fun onDestroyView() {

        binding.rvTopic.adapter = null
        _binding = null

        super.onDestroyView()
    }
}
package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.Models.hocTapOptions
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.UI.HocTapOptionsAdapter
import com.example.EnglishWithStork.UI.TopicAdapter
import com.example.EnglishWithStork.databinding.FragmentHocTapBinding

class HocTap : Fragment() {

    private var _binding: FragmentHocTapBinding? = null

    private val binding: FragmentHocTapBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHocTapBinding.inflate(
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

        setupHocTapOptions()
        setupTopics()
    }

    /**
     * RecyclerView phía trên:
     * FlashCard, Kiểm tra, Ôn tập.
     */
    private fun setupHocTapOptions() {

        val listHocTapOptions = listOf(

            hocTapOptions(
                name = "FlashCard",
                description = "Học từ vựng qua thẻ",
                image_description = R.drawable.ic_flashcard
            ),

            hocTapOptions(
                name = "Kiểm tra",
                description = "Làm bài kiểm tra nhanh",
                image_description = R.drawable.ic_exam
            ),

            hocTapOptions(
                name = "Ôn tập",
                description = "Ôn tập các từ đã học",
                image_description = R.drawable.ic_ontap
            )
        )

        binding.rvItemHoctapOptions.apply {

            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = HocTapOptionsAdapter(
                listHocTapOptions
            )

            setHasFixedSize(true)
        }
    }

    /**
     * RecyclerView danh sách 16 chủ đề.
     */
    private fun setupTopics() {

        val listTopics = listOf(

            Topic(
                topic_name = "Số đếm và phép tính",
                description = "30 từ",
                image_description = R.drawable.ic_tinhtoan_sodem,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Gia đình",
                description = "29 từ",
                image_description = R.drawable.family,
                isCompleted = false,
                topic_id = 2
            ),

            Topic(
                topic_name = "Nghề nghiệp",
                description = "45 từ",
                image_description = R.drawable.jobs,
                isCompleted = false,
                topic_id = 3
            ),

            Topic(
                topic_name = "Thời tiết",
                description = "40 từ",
                image_description = R.drawable.ic_thoitiet1,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Quần áo",
                description = "40 từ",
                image_description = R.drawable.ic_clothes,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Tính cách",
                description = "20 từ",
                image_description = R.drawable.ic_tinhcach,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Bộ phận cơ thể",
                description = "20 từ",
                image_description = R.drawable.ic_bophancothe,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Ngoại hình",
                description = "20 từ",
                image_description = R.drawable.ic_ngoaihinh,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Cảm xúc",
                description = "20 từ",
                image_description = R.drawable.ic_camxuc,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Rau củ",
                description = "20 từ",
                image_description = R.drawable.ic_raucu,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Hoa quả",
                description = "20 từ",
                image_description = R.drawable.fruits,
                isCompleted = false,
                topic_id = 10
            ),

            Topic(
                topic_name = "Động vật",
                description = "40 từ",
                image_description = R.drawable.animals,
                isCompleted = false,
                topic_id = 12
            ),

            Topic(
                topic_name = "Đồ ăn & Đồ uống",
                description = "40 từ",
                image_description = R.drawable.ic_doan_douong,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Quốc gia",
                description = "20 từ",
                image_description = R.drawable.ic_quocgia,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Mua sắm",
                description = "20 từ",
                image_description = R.drawable.ic_muasam,
                isCompleted = false,
                topic_id = 0
            ),

            Topic(
                topic_name = "Sức khỏe",
                description = "20 từ",
                image_description = R.drawable.ic_suckhoe,
                isCompleted = false,
                topic_id = 0
            )
        )

        binding.rvTopic.apply {

            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )

            adapter = TopicAdapter(
                listTopic = listTopics
            ) { topic ->

                openVocabularyList(topic)
            }

            setHasFixedSize(true)
        }
    }

    /**
     * Mở màn hình danh sách từ tương ứng với topic_id.
     */
    private fun openVocabularyList(
        topic: Topic
    ) {

        // topic_id = 0 nghĩa là bạn chưa gán ID cho chủ đề.
        if (topic.topic_id <= 0) {
            return
        }

        val fragment = VocabListFragment.newInstance(
            topicId = topic.topic_id,
            topicName = topic.topic_name
        )

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frame_layout,
                fragment
            )
            .addToBackStack("vocab_list")
            .commit()
    }

    override fun onDestroyView() {

        binding.rvItemHoctapOptions.adapter = null
        binding.rvTopic.adapter = null

        _binding = null

        super.onDestroyView()
    }
}
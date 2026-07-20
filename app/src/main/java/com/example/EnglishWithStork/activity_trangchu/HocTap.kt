package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.databinding.FragmentHocTapBinding
import com.example.EnglishWithStork.Models.hocTapOptions
import com.example.EnglishWithStork.UI.HocTapOptionsAdapter
import com.example.EnglishWithStork.UI.TopicAdapter

class HocTap : Fragment() {

    private var _binding: FragmentHocTapBinding? = null
    private  val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHocTapBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listHocTapOptions = listOf(
            hocTapOptions("FlashCard", "Học từ vựng qua thẻ", R.drawable.ic_flashcard),
            hocTapOptions("Kiểm tra", "Làm bài kiểm tra nhanh", R.drawable.ic_exam),
            hocTapOptions("Ôn tập", "Ôn tập các từ đã học", R.drawable.ic_ontap)
        )
        binding.rvItemHoctapOptions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemHoctapOptions.adapter = HocTapOptionsAdapter(listHocTapOptions)

        val listTopics = listOf(
            Topic("Gia đình", "29 từ", R.drawable.family, false),
            Topic("Thời tiết", "40 từ", R.drawable.jobs, false),
            Topic("Nghề nghiệp", "45 từ", R.drawable.fruits, false),
            Topic("Quần áo", "40 từ", R.drawable.animals, false),
            Topic("Tính cách", "20 từ", R.drawable.fruits, false),
            Topic("Rau củ", "20 từ", R.drawable.animals, false),
            Topic("Động vật", "25 từ", R.drawable.fruits, false),
            Topic("Đồ ăn", "20 từ", R.drawable.animals, false),
            Topic("Trường học", "30 từ", R.drawable.animals, false),
            Topic("Màu sắc", "15 từ", R.drawable.animals, false),
            Topic("Giao thông", "20 từ", R.drawable.animals, false)
        )
        binding.rvTopic.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
        binding.rvTopic.adapter = TopicAdapter(listTopics)
        }
    }
}
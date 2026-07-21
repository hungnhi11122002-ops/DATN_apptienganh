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
            Topic("Số đếm và phép tính", "30 từ", R.drawable.ic_tinhtoan_sodem, false),
            Topic("Gia đình", "29 từ", R.drawable.family, false),
            Topic("Nghề nghiệp", "45 từ", R.drawable.jobs, false),
            Topic("Thời tiết", "40 từ", R.drawable.ic_thoitiet1, false),
            Topic("Quần áo", "40 từ", R.drawable.ic_clothes, false),
            Topic("Tính cách", "20 từ", R.drawable.ic_tinhcach, false),
            Topic("Bộ phận cơ thể", "20 từ", R.drawable.ic_bophancothe, false),
            Topic("Ngoại hình", "20 từ", R.drawable.ic_ngoaihinh, false),
            Topic("Cảm xúc", "20 từ", R.drawable.ic_camxuc, false),
            Topic("Rau củ", "20 từ", R.drawable.ic_raucu, false),
            Topic("Hoa quả", "20 từ", R.drawable.fruits, false),
            Topic("Động vật", "40 từ", R.drawable.animals, false),
            Topic("Đồ ăn & Đồ uống", "40 từ", R.drawable.ic_doan_douong, false),
            Topic("Quốc gia", "20 từ", R.drawable.ic_quocgia, false),
            Topic("Mua sắm", "20 từ", R.drawable.ic_muasam, false),
            Topic("Sức khỏe", "20 từ", R.drawable.ic_suckhoe, false)
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
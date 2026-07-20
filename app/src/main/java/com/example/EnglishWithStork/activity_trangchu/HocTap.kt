package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.databinding.FragmentHocTapBinding
import com.example.EnglishWithStork.Models.hocTapOptions
import com.example.EnglishWithStork.UI.HocTapOptionsAdapter

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
            hocTapOptions("FlashCard","Học từ vựng qua thẻ",R.drawable.ic_flashcard),
            hocTapOptions("Kiểm tra","Làm bài kiểm tra nhanh",R.drawable.ic_exam),
            hocTapOptions("Ôn tập","Ôn tập các từ đã học",R.drawable.ic_ontap)
        )
        binding.rvItemHoctapOptions.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemHoctapOptions.adapter = HocTapOptionsAdapter(listHocTapOptions)
    }


}
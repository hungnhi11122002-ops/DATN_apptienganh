package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.Models.quick_practise
import com.example.EnglishWithStork.UI.PractiseAdapter
import com.example.EnglishWithStork.UI.TopicAdapter
import com.example.EnglishWithStork.databinding.FragmentTrangChuBinding

class TrangChu : Fragment() {

    private var _binding: FragmentTrangChuBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrangChuBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTopic =listOf(
            Topic("Gia đình", "29 từ",R.drawable.family,false),
            Topic("Nghề nghiệp", "30 từ",R.drawable.jobs,false)
        )
        binding.rvItemTopic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemTopic.adapter = TopicAdapter(listTopic)

        val listTopic2 =listOf(
            Topic("Trái cây", "20 từ",R.drawable.fruits,false),
            Topic("Động vật", "15 từ",R.drawable.animals,false),

        )
        binding.rvItemTopic2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemTopic2.adapter = TopicAdapter(listTopic2)

        val listquick_practise = listOf(
            quick_practise("Từ vựng", "Học từ vựng mỗi ngày",R.drawable.ic_prac),
            quick_practise("Kiểm tra", "Kiểm tra kiến thức",R.drawable.ic_exam)
        )
        binding.rvItemLuyentap.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemLuyentap.adapter = PractiseAdapter(listquick_practise)
    }

    override fun onDestroyView() {
        binding.rvItemTopic.adapter = null
        binding.rvItemTopic2.adapter = null
        binding.rvItemLuyentap.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
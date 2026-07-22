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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

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

        val listTopic = listOf(
            Topic(
                "Gia đình",
                "25 từ",
                R.drawable.family,
                false,
                2
            ),
            Topic(
                "Nghề nghiệp",
                "25 từ",
                R.drawable.jobs,
                false,
                3
            ),
            Topic(
                "Trái cây",
                "25 từ",
                R.drawable.fruits,
                false,
                10
            ),
            Topic(
                "Động vật",
                "25 từ",
                R.drawable.animals,
                false,
                12
            )
        )

        binding.rvItemTopic.apply {

            // 2: RecyclerView có 2 hàng.
            // HORIZONTAL: cuộn từ trái sang phải.
            layoutManager = GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.HORIZONTAL,
                false
            )

            adapter = TopicAdapter(listTopic) { selectedTopic ->
                openVocabularyList(selectedTopic)
            }

            // Chỉ nên dùng khi kích thước RecyclerView không đổi theo dữ liệu.
            setHasFixedSize(true)
        }

        val listquick_practise = listOf(
            quick_practise("Từ vựng", "Học từ vựng mỗi ngày",R.drawable.ic_prac),
            quick_practise("Kiểm tra", "Kiểm tra kiến thức",R.drawable.ic_exam)
        )
        binding.rvItemLuyentap.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvItemLuyentap.adapter = PractiseAdapter(listquick_practise)
    }

    private fun openVocabularyList(topic: Topic) {

        // Không cho mở khi chủ đề chưa có ID hợp lệ
        if (topic.topic_id <= 0) {

            Toast.makeText(
                requireContext(),
                "Chủ đề này chưa có dữ liệu từ vựng",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Tạo màn hình danh sách từ và truyền ID + tên chủ đề
        val vocabListFragment = VocabListFragment.newInstance(
            topicId = topic.topic_id,
            topicName = topic.topic_name
        )

        // Thay Fragment Trang chủ bằng Fragment danh sách từ
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frame_layout,
                vocabListFragment
            )
            .addToBackStack("vocab_list")
            .commit()
    }

    override fun onDestroyView() {
        binding.rvItemTopic.adapter = null
        binding.rvItemLuyentap.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
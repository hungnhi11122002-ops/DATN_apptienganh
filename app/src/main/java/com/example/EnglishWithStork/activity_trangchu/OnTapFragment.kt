package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.EnglishWithStork.databinding.FragmentOnTapBinding

class OnTapFragment : Fragment() {

    private var _binding: FragmentOnTapBinding? = null

    private val binding: FragmentOnTapBinding
        get() = _binding!!


    private var topicId: Int = 0
    private var topicName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicId = arguments?.getInt(ARG_TOPIC_ID) ?: 0
        topicName = arguments?.getString(ARG_TOPIC_NAME) ?: ""
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOnTapBinding.inflate(
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

        binding.tvSubtitle.text = "Chủ đề: $topicName"
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


    companion object {

        private const val ARG_TOPIC_ID = "topic_id"
        private const val ARG_TOPIC_NAME = "topic_name"


        fun newInstance(
            topicId: Int,
            topicName: String
        ): OnTapFragment {

            return OnTapFragment().apply {

                arguments = Bundle().apply {

                    putInt(
                        ARG_TOPIC_ID,
                        topicId
                    )

                    putString(
                        ARG_TOPIC_NAME,
                        topicName
                    )
                }
            }
        }
    }
}
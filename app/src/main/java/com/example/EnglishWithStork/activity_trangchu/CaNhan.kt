package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.SessionManager
import com.example.EnglishWithStork.activity_dangnhap
import com.example.EnglishWithStork.databinding.FragmentCaNhanBinding

class CaNhan : Fragment() {
    private var _binding: FragmentCaNhanBinding? = null

    private  val binding: FragmentCaNhanBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCaNhanBinding.inflate(
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

        binding.btnLogout.setOnClickListener {
            SessionManager(requireContext()).clearSession()

            val i1 = Intent(requireContext(), activity_dangnhap::class.java)

            i1.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(i1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.SessionManager
import com.example.EnglishWithStork.databinding.FragmentThongTinCaNhanBinding
import kotlinx.coroutines.launch

class ThongTinCaNhanFragment : Fragment() {

    private var _binding:
            FragmentThongTinCaNhanBinding? = null

    private val binding:
            FragmentThongTinCaNhanBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentThongTinCaNhanBinding.inflate(
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

        database =
            AppDatabase.getDatabase(
                requireContext()
            )

        sessionManager =
            SessionManager(
                requireContext()
            )

        setupListeners()

        loadUserInformation()
    }


    private fun setupListeners() {

        binding.btnBack.setOnClickListener {

            parentFragmentManager
                .popBackStack()
        }
    }


    private fun loadUserInformation() {

        val userId =
            sessionManager.getUserId()

        if (userId == -1) {

            Toast.makeText(
                requireContext(),
                "Không tìm thấy phiên đăng nhập",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        viewLifecycleOwner
            .lifecycleScope
            .launch {

                val user =
                    database
                        .userDao()
                        .getUserById(
                            userId
                        )


                if (user == null) {

                    Toast.makeText(
                        requireContext(),
                        "Không tìm thấy thông tin người dùng",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }


                // HỌ VÀ TÊN
                binding.tvFullName.text =
                    if (user.hoten.isNotBlank()) {

                        user.hoten

                    } else {

                        "Chưa cập nhật họ tên"
                    }


                // TÊN ĐĂNG NHẬP
                binding.tvUsername.text =
                    user.tendangnhap


                // NGÀY SINH
                binding.tvBirthDate.text =
                    if (user.ngaysinh.isNotBlank()) {

                        user.ngaysinh

                    } else {

                        "Chưa cập nhật"
                    }


                // GIỚI TÍNH
                binding.tvGender.text =
                    if (user.gioitinh.isNotBlank()) {

                        user.gioitinh

                    } else {

                        "Chưa cập nhật"
                    }
            }
    }


    override fun onDestroyView() {

        _binding = null

        super.onDestroyView()
    }
}
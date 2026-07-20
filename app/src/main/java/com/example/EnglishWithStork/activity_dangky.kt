package com.example.EnglishWithStork

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.EnglishWithStork.databinding.LayoutDangkyBinding
import android.view.View
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.example.EnglishWithStork.RoomDatabase.Entity.Entity_user
import kotlinx.coroutines.launch

class activity_dangky: AppCompatActivity() {
    //Khởi tạo biến binding
    private lateinit var binding: LayoutDangkyBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //khởi tạo binding
        binding = LayoutDangkyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext)

        binding.btdangky.setOnClickListener {
            ktdky();
        }

        binding.btdangnhap1.setOnClickListener {
            val i1 = Intent(this@activity_dangky, activity_dangnhap::class.java)
            startActivity(i1)
            finish()
        }

    }
    private fun ktdky(){
        val tk = binding.edttk.text.toString().trim()
        val mk = binding.edtmk.text.toString().trim()
        val xnmk = binding.edtmk2.text.toString().trim()
        val ns = binding.edtngaysinh.text.toString().trim()
        val cb1 = binding.cb1.isChecked

        if(tk.isEmpty())
        {
            binding.edttk.error ="Vui lòng nhập tên đăng nhập!"
            binding.edttk.requestFocus()
            return
        }
        if(mk.isEmpty()){
            binding.edtmk.error = "Vui lòng nhập mật khẩu!"
            binding.edtmk.requestFocus()
            return
        }
        if(xnmk.isEmpty()){
            binding.edtmk2.error = "Vui lòng nhập mật khẩu!"
            binding.edtmk2.requestFocus()
            return
        }
        if(xnmk != mk)
        {
            binding.edtmk2.error = "Mật khẩu không khớp! Vui lòng nhập lại!"
            binding.edtmk2.requestFocus()
            return
        }
        if(ns.isEmpty()){
            binding.edtngaysinh.error = "Vui lòng điền ngày, tháng, năm sinh!"
            binding.edtngaysinh.requestFocus()
            return
        }

        //Xử lý radioButton giới tính
        val idgt = binding.group1.checkedRadioButtonId
        if(idgt == -1){
            Toast.makeText(this,"Vui lòng chọn giới tính!", Toast.LENGTH_SHORT).show()
            return
        }
        val rdbgioitinh = findViewById<RadioButton>(idgt)
        val gt = rdbgioitinh.text.toString()

        if (!cb1)
        {
            Toast.makeText(this,"Bạn phải đồng ý với tất cả điều khoản sử dụng!", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = Entity_user(
            tendangnhap = tk,
            matkhau = mk,
            ngaysinh = ns,
            gioitinh = gt
        )
        //Insert thông tin user vào RoomDatabase
        lifecycleScope.launch {
            val result = database.userDao().insertuser(newUser)
            if(result == -1L){
                binding.edttk.error = "Tên đăng nhập đã tồn tại!"
                binding.edttk.requestFocus()

                Toast.makeText(this@activity_dangky,"Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
                return@launch
            }
            else{
                Toast.makeText(this@activity_dangky,"Đã tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()
                xoadulieu_danhap()
                val i1 = Intent(this@activity_dangky, activity_dangnhap::class.java)
                startActivity(i1)
                finish()
            }
        }


    }

    fun xoadulieu_danhap(){
        binding.edttk.text?.clear()
        binding.edtmk.text?.clear()
        binding.edtmk2.text?.clear()
        binding.edtngaysinh.text?.clear()
        binding.group1.clearCheck()
        binding.cb1.isChecked=false
    }
}
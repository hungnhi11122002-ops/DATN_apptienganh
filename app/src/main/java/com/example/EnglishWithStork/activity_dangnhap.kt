package com.example.EnglishWithStork

import android.os.Bundle
import android.os.PersistableBundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.EnglishWithStork.databinding.LayoutDangnhapBinding
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.activity_trangchu.activity_trangchu
import kotlinx.coroutines.launch

class activity_dangnhap: AppCompatActivity() {
    private lateinit var binding: LayoutDangnhapBinding
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDangnhapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext)
        binding.btdangnhap.setOnClickListener {
            xulydangnhap()
        }

        binding.tv3.setOnClickListener {
            val i1 = Intent(this@activity_dangnhap, activity_dangky::class.java)
            startActivity(i1)
            finish()
        }
    }
    private fun xulydangnhap(){
        val tk = binding.edttk.text.toString().trim()
        val mk = binding.edtmk.text.toString().trim()
        if (tk.isEmpty()) {
            binding.edttk.error = "Vui lòng điền tên đăng nhập!"
            binding.edttk.requestFocus()
            return
        }
        if (mk.isEmpty()){
            binding.edtmk.error = "Vui lòng điền mật khẩu!"
            binding.edtmk.requestFocus()
            return
        }

        //Gọi DAO để kiểm tra tài khỏ trong RoomDatabase
        lifecycleScope.launch {
            val user = database.userDao().login(
                tendangnhap = tk,
                matkhau = mk
            )

            if(user != null) {
                Toast.makeText(
                    this@activity_dangnhap,
                    "Đăng nhập thành công!",
                    Toast.LENGTH_SHORT
                ).show()
                val i2 = Intent(this@activity_dangnhap, activity_trangchu::class.java)
                startActivity(i2)
                finish()
            }
            else{
                Toast.makeText(this@activity_dangnhap,
                    "Tên đăng nhập hoặc mật khẩu không đúng!",
                    Toast.LENGTH_SHORT).show()
            }


        }
    }
}

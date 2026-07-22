package com.example.EnglishWithStork

import android.content.Context
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

//  XỬ LÝ LƯU ĐĂNG NHẬP
    private val sharePreferences by lazy {
        getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDangnhapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(applicationContext)

//      Khôi phục tài khoản đã lưu khi mở màn hình đăng nhập
        khoiPhucTaiKhoanDaLuu()

        binding.btdangnhap.setOnClickListener {
            xulydangnhap()
        }

        binding.tv3.setOnClickListener {
            val i1 = Intent(this@activity_dangnhap, activity_dangky::class.java)
            startActivity(i1)
            finish()
        }
    }

    private fun khoiPhucTaiKhoanDaLuu(){
        val daGhiNho = sharePreferences.getBoolean(KEY_REMEMBER,false)
        if(daGhiNho){
            val tkDaLuu = sharePreferences.getString(KEY_USERNAME,"")
            val mkDaLuu = sharePreferences.getString(KEY_PASSWORD,"")
            binding.edttk.setText(tkDaLuu)
            binding.edtmk.setText(mkDaLuu)
            binding.cb1.isChecked = true
        }
        else{
            binding.cb1.isChecked = false
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

                SessionManager(
                    this@activity_dangnhap
                ).saveUserId(user.id)

//              Chỉ lưu TK sau khi đã đăng nhập thành công
                xulyGhiNhoDangNhap(
                    taiKhoan = tk,
                    matKhau = mk
                )

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
    private fun xulyGhiNhoDangNhap(
        taiKhoan: String,
        matKhau: String
    ){
        sharePreferences.edit().apply(){
            if(binding.cb1.isChecked){
                putBoolean(KEY_REMEMBER,true)
                putString(KEY_USERNAME,taiKhoan)
                putString(KEY_PASSWORD,matKhau)
            }
            else{
                remove(KEY_REMEMBER)
                remove(KEY_USERNAME)
                remove(KEY_PASSWORD)
            }
            apply()
        }
    }

    companion object{
        private const val PREF_LOGIN = "login_preferences"

        private const val KEY_REMEMBER = "remember_login"
        private const val KEY_USERNAME = "saved_username"
        private const val KEY_PASSWORD = "saved_password"
    }
}


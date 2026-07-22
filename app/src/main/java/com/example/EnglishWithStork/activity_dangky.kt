package com.example.EnglishWithStork

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.EnglishWithStork.databinding.LayoutDangkyBinding
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.example.EnglishWithStork.RoomDatabase.Entity.Entity_user
import kotlinx.coroutines.launch
import android.text.Editable
import android.text.TextWatcher
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        setupNgaySinhInput()

        binding.btdangky.setOnClickListener {
            ktdky();
        }

        binding.btdangnhap1.setOnClickListener {
            val i1 = Intent(this@activity_dangky, activity_dangnhap::class.java)
            startActivity(i1)
            finish()
        }

    }

    private fun setupNgaySinhInput() {

        val dateWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Không xử lý
            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // Không xử lý
            }

            override fun afterTextChanged(
                editable: Editable?
            ) {
                if (editable == null) return

                val currentText = editable.toString()

                // Chỉ lấy tối đa 8 chữ số: ddMMyyyy
                val numbers = currentText
                    .filter { it.isDigit() }
                    .take(8)

                val formattedText = buildString {

                    numbers.forEachIndexed { index, character ->

                        append(character)

                        // Sau 2 số ngày
                        if (index == 1 && numbers.length > 2) {
                            append("/")
                        }

                        // Sau 2 số tháng
                        if (index == 3 && numbers.length > 4) {
                            append("/")
                        }
                    }
                }

                if (currentText != formattedText) {

                    /*
                     * Tạm thời gỡ TextWatcher trước khi setText,
                     * tránh afterTextChanged bị gọi đệ quy.
                     */
                    binding.edtngaysinh.removeTextChangedListener(this)

                    binding.edtngaysinh.setText(formattedText)

                    val cursorPosition = formattedText.length
                        .coerceAtMost(
                            binding.edtngaysinh.text.length
                        )

                    binding.edtngaysinh.setSelection(
                        cursorPosition
                    )

                    // Gắn lại TextWatcher
                    binding.edtngaysinh.addTextChangedListener(this)
                }

                binding.edtngaysinh.error = null
            }
        }

        binding.edtngaysinh.addTextChangedListener(
            dateWatcher
        )
    }



    private fun ktdky() {

        val tk =
            binding.edttk.text.toString().trim()

        val mk =
            binding.edtmk.text.toString().trim()

        val xnmk =
            binding.edtmk2.text.toString().trim()

        val ns =
            binding.edtngaysinh.text.toString().trim()

        val cb1 =
            binding.cb1.isChecked

        if (tk.isEmpty()) {
            binding.edttk.error =
                "Vui lòng nhập tên đăng nhập!"

            binding.edttk.requestFocus()
            return
        }

        if (mk.isEmpty()) {
            binding.edtmk.error =
                "Vui lòng nhập mật khẩu!"

            binding.edtmk.requestFocus()
            return
        }

        if (xnmk.isEmpty()) {
            binding.edtmk2.error =
                "Vui lòng xác nhận mật khẩu!"

            binding.edtmk2.requestFocus()
            return
        }

        if (xnmk != mk) {
            binding.edtmk2.error =
                "Mật khẩu không khớp! Vui lòng nhập lại!"

            binding.edtmk2.requestFocus()
            return
        }

        if (ns.isEmpty()) {
            binding.edtngaysinh.error =
                "Vui lòng nhập ngày sinh!"

            binding.edtngaysinh.requestFocus()
            return
        }

        if (!isNgaySinhHopLe(ns)) {
            binding.edtngaysinh.error =
                "Ngày sinh không hợp lệ. Vui lòng nhập đúng định dạng DD/MM/YYYY"

            binding.edtngaysinh.requestFocus()
            return
        }

        val idgt =
            binding.group1.checkedRadioButtonId

        if (idgt == -1) {
            Toast.makeText(
                this,
                "Vui lòng chọn giới tính!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val rdbgioitinh =
            findViewById<RadioButton>(idgt)

        val gt =
            rdbgioitinh.text.toString()

        if (!cb1) {
            Toast.makeText(
                this,
                "Bạn phải đồng ý với tất cả điều khoản sử dụng!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val newUser =
            Entity_user(
                tendangnhap = tk,
                matkhau = mk,
                ngaysinh = ns,
                gioitinh = gt
            )

        lifecycleScope.launch {

            val result =
                database.userDao()
                    .insertuser(newUser)

            if (result == -1L) {

                binding.edttk.error =
                    "Tên đăng nhập đã tồn tại!"

                binding.edttk.requestFocus()

                Toast.makeText(
                    this@activity_dangky,
                    "Tên đăng nhập đã tồn tại",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            Toast.makeText(
                this@activity_dangky,
                "Đã tạo tài khoản thành công!",
                Toast.LENGTH_SHORT
            ).show()

            xoadulieu_danhap()

            val intent =
                Intent(
                    this@activity_dangky,
                    activity_dangnhap::class.java
                )

            startActivity(intent)
            finish()
        }
    }
    private fun isNgaySinhHopLe(
        ngaySinh: String
    ): Boolean {

        /*
         * Phải đúng chính xác:
         * 2 số ngày / 2 số tháng / 4 số năm
         */
        val dateRegex =
            Regex("""^\d{2}/\d{2}/\d{4}$""")

        if (!dateRegex.matches(ngaySinh)) {
            return false
        }

        /*
         * isLenient = false:
         * Không tự chuyển những ngày sai.
         *
         * Ví dụ không cho:
         * 31/02/2000
         * 32/01/2000
         * 15/13/2000
         */
        val formatter =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).apply {
                isLenient = false
            }

        return try {

            val birthDate =
                formatter.parse(ngaySinh)
                    ?: return false

            /*
             * Không cho ngày sinh lớn hơn ngày hiện tại.
             */
            !birthDate.after(Date())

        } catch (exception: ParseException) {
            false
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
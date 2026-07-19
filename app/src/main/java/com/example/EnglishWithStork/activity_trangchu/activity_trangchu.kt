package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.databinding.LayoutTrangchuBinding

class activity_trangchu: AppCompatActivity() {
    private lateinit var binding: LayoutTrangchuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutTrangchuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_trangchu->{
                    replaceFragment(TrangChu())
                    true
                }
                R.id.nav_hoctap->{
                    replaceFragment(HocTap())
                    true
                }
                R.id.nav_sotay->{
                    replaceFragment(SoTay())
                    true
                }
                R.id.nav_canhan->{
                    replaceFragment(CaNhan())
                    true
                }
                else -> false
            }
        }
        if (savedInstanceState == null) {
//            replaceFragment(TrangChu())
            binding.BottomNavigation.selectedItemId = R.id.nav_trangchu
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

}
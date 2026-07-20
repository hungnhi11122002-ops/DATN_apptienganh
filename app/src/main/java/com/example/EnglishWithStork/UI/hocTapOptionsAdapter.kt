package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.hocTapOptions
import com.example.EnglishWithStork.databinding.FragmentHocTapBinding
import com.example.EnglishWithStork.databinding.ItemHoctapOptionsBinding

class HocTapOptionsAdapter(
    private var listHocTap: List<hocTapOptions>
): RecyclerView.Adapter<HocTapOptionsAdapter.HocTapOptionsViewHolder>(){
    class HocTapOptionsViewHolder(
        val binding: ItemHoctapOptionsBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): HocTapOptionsViewHolder {
        val binding = ItemHoctapOptionsBinding.inflate(LayoutInflater.from(p0.context),
            p0,
            false
        )
        return HocTapOptionsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HocTapOptionsViewHolder,
        position: Int
    ) {
        val hoctap = listHocTap[position]
        holder.binding.tvFeatureName.text = hoctap.name
        holder.binding.tvFeatureDescription.text = hoctap.name
        holder.binding.imgFeature.setImageResource(hoctap.image_description)
    }

    override fun getItemCount(): Int {
        return listHocTap.size
    }
}
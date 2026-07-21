package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.hocTapOptions
import com.example.EnglishWithStork.databinding.ItemHoctapOptionsBinding

class HocTapOptionsAdapter(
    private val listHocTap: List<hocTapOptions>
) : RecyclerView.Adapter<
        HocTapOptionsAdapter.HocTapOptionsViewHolder
        >() {

    class HocTapOptionsViewHolder(
        val binding: ItemHoctapOptionsBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HocTapOptionsViewHolder {

        val binding = ItemHoctapOptionsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return HocTapOptionsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HocTapOptionsViewHolder,
        position: Int
    ) {

        val hocTapOption = listHocTap[position]

        holder.binding.tvFeatureName.text =
            hocTapOption.name

        holder.binding.tvFeatureDescription.text =
            hocTapOption.description

        holder.binding.imgFeature.setImageResource(
            hocTapOption.image_description
        )
    }

    override fun getItemCount(): Int {
        return listHocTap.size
    }
}
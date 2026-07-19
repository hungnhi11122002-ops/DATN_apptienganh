package com.example.EnglishWithStork.UI


import android.view.LayoutInflater
import com.example.EnglishWithStork.Models.quick_practise
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.example.EnglishWithStork.databinding.ItemQuickpractiseBinding


class PractiseAdapter(
    private var listPractise: List<quick_practise>
): RecyclerView.Adapter<PractiseAdapter.PractiseViewHolder>(){
    class PractiseViewHolder(
        val binding: ItemQuickpractiseBinding
    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): PractiseAdapter.PractiseViewHolder {
        val binding = ItemQuickpractiseBinding.inflate(LayoutInflater.from(p0.context),
            p0,
            false
        )
        return PractiseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PractiseAdapter.PractiseViewHolder,
        position: Int
    ) {
        val prac = listPractise[position]
        holder.binding.tvPractiseName.text = prac.name
        holder.binding.tvDescription.text = prac.name
        holder.binding.imgPractise.setImageResource(prac.image_description)

    }

    override fun getItemCount(): Int {
        return listPractise.size
    }

}
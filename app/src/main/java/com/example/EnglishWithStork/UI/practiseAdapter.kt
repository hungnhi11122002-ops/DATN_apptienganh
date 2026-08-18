package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.quick_practise
import com.example.EnglishWithStork.databinding.ItemQuickpractiseBinding

class PractiseAdapter(
    private var listPractise: List<quick_practise>,
    private val onItemClick: (quick_practise) -> Unit
) : RecyclerView.Adapter<PractiseAdapter.PractiseViewHolder>() {

    class PractiseViewHolder(
        val binding: ItemQuickpractiseBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PractiseViewHolder {

        val binding = ItemQuickpractiseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PractiseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PractiseViewHolder,
        position: Int
    ) {

        val practise = listPractise[position]

        holder.binding.tvPractiseName.text = practise.name
        holder.binding.tvDescription.text = practise.description
        holder.binding.imgPractise.setImageResource(practise.image_description)

        holder.binding.root.setOnClickListener {
            onItemClick(practise)
        }
    }

    override fun getItemCount(): Int {
        return listPractise.size
    }
}
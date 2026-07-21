package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.databinding.ItemVocabularyBinding
//VocabularyAdapter là một class kế thừa từ ListAdapter hoặc RecyclerView.Adapter
//chứ không như các Adapter khác
class VocabularyAdapter :
    ListAdapter<VocabularyEntity, VocabularyAdapter.VocabularyViewHolder>(
        VocabularyDiffCallback()
    ) {

    class VocabularyViewHolder(
        val binding: ItemVocabularyBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VocabularyViewHolder {

        val binding = ItemVocabularyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return VocabularyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: VocabularyViewHolder,
        position: Int
    ) {
        val vocabulary = getItem(position)

        holder.binding.apply {

            tvSortOrder.text = vocabulary.sortOrder
                .toString()
                .padStart(2, '0')

            tvEnglish.text = vocabulary.english
            tvVietnamese.text = vocabulary.vietnamese

            tvWordClass.text = vocabulary.wordClass.orEmpty()
            tvWordClass.isVisible =
                !vocabulary.wordClass.isNullOrBlank()

            tvPhonetic.text = vocabulary.phonetic.orEmpty()
            tvPhonetic.isVisible =
                !vocabulary.phonetic.isNullOrBlank()
        }
    }

    private class VocabularyDiffCallback :
        DiffUtil.ItemCallback<VocabularyEntity>() {

        override fun areItemsTheSame(
            oldItem: VocabularyEntity,
            newItem: VocabularyEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: VocabularyEntity,
            newItem: VocabularyEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
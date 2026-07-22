package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.databinding.ItemVocabularyBinding
import androidx.core.view.isVisible

class VocabularyAdapter(
    private val showTopic: Boolean = true,
    private val onSpeakClick: (VocabularyEntity) -> Unit,
    private val onSaveClick: (VocabularyEntity, Boolean) -> Unit
) : ListAdapter<VocabularyEntity, VocabularyAdapter.VocabularyViewHolder>(
    VocabularyDiffCallback()
) {

    /**
     * Chứa ID những từ đang được lưu trong sổ tay.
     */
    private var savedVocabularyIds: Set<Int> =
        emptySet()

    class VocabularyViewHolder(
        val binding: ItemVocabularyBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VocabularyViewHolder {

        val binding =
            ItemVocabularyBinding.inflate(
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

        val isSaved =
            savedVocabularyIds.contains(vocabulary.id)

        holder.binding.apply {

            tvSortOrder.isVisible = showTopic

            if (showTopic) {
                tvSortOrder.text = vocabulary.sortOrder.toString()
            } else {
                tvSortOrder.text = ""
            }

            tvEnglish.text =
                vocabulary.english

            tvVietnamese.text =
                vocabulary.vietnamese

            tvWordClass.text =
                vocabulary.wordClass.orEmpty()

            tvWordClass.isVisible =
                !vocabulary.wordClass.isNullOrBlank()

            tvPhonetic.text =
                vocabulary.phonetic.orEmpty()

            tvPhonetic.isVisible =
                !vocabulary.phonetic.isNullOrBlank()

            btnSave.setImageResource(
                if (isSaved) {
                    R.drawable.ic_bookmark_24
                } else {
                    R.drawable.ic_bookmark_border_24
                }
            )

            btnSave.contentDescription =
                if (isSaved) {
                    "Xóa khỏi sổ tay"
                } else {
                    "Lưu vào sổ tay"
                }

            btnSpeak.setOnClickListener {
                onSpeakClick(vocabulary)
            }

            btnSave.setOnClickListener {
                onSaveClick(
                    vocabulary,
                    isSaved
                )
            }
        }
    }

    /**
     * Cập nhật trạng thái icon lưu.
     *
     * Danh sách mỗi chủ đề của bạn không quá lớn,
     * nên notifyDataSetChanged() là đủ đơn giản và ổn định.
     */
    fun setSavedVocabularyIds(
        newIds: Set<Int>
    ) {
        if (savedVocabularyIds == newIds) {
            return
        }

        savedVocabularyIds = newIds
        notifyDataSetChanged()
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
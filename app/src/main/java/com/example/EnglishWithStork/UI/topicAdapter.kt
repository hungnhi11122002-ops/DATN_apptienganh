package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.databinding.ItemTopicsBinding
import androidx.core.util.TypedValueCompat.dpToPx

class TopicAdapter(
    private var listTopic: List<Topic>,
    private val fullWidth: Boolean = false,
    // Hàm được gọi khi người dùng bấm một chủ đề
    private val onTopicClick: (Topic) -> Unit = {}

) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    class TopicViewHolder(
        val binding: ItemTopicsBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicViewHolder {

        val binding = ItemTopicsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val layoutParams = binding.root.layoutParams

        layoutParams.width = if (fullWidth) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            dpToPx(parent, 180)
        }

        binding.root.layoutParams = layoutParams

        return TopicViewHolder(binding)
    }
    private fun dpToPx(parent: ViewGroup, dp: Int): Int {
        return (dp * parent.resources.displayMetrics.density).toInt()
    }

    override fun onBindViewHolder(
        holder: TopicViewHolder,
        position: Int
    ) {
        val topic = listTopic[position]

        holder.binding.tvTopicName.text = topic.topic_name
        holder.binding.tvDescription.text = topic.description
        holder.binding.imgTopic.setImageResource(topic.image_description)
        holder.binding.root.setOnClickListener {
            onTopicClick(topic)
        }
        // Bấm vào toàn bộ item
        holder.binding.root.setOnClickListener {
            onTopicClick(topic)
        }
    }

    override fun getItemCount(): Int {
        return listTopic.size
    }
}
package com.example.EnglishWithStork.UI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.EnglishWithStork.Models.Topic
import com.example.EnglishWithStork.databinding.ItemTopicsBinding

class TopicAdapter(
    private var listTopic: List<Topic>
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {
    class TopicViewHolder(
        val binding: ItemTopicsBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): TopicViewHolder {
        val binding = ItemTopicsBinding.inflate(
            LayoutInflater.from(p0.context),
            p0,
            false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(
        p0: TopicViewHolder,
        position: Int
    ) {
        val topic = listTopic[position]
        p0.binding.tvTopicName.text = topic.topic_name
        p0.binding.tvDescription.text = topic.description

    }

    override fun getItemCount(): Int {
        return listTopic.size
    }
}






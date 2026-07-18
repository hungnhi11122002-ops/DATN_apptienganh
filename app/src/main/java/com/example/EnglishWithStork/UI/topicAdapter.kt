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
        holder: TopicViewHolder,
        position: Int
    ) {
        val topic = listTopic[position]
        holder.binding.tvTopicName.text = topic.topic_name
        holder.binding.tvDescription.text = topic.description
        holder.binding.imgTopic.setImageResource(topic.image_description)

    }

    override fun getItemCount(): Int {
        return listTopic.size
    }

}






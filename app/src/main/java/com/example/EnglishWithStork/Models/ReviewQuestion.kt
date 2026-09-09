package com.example.EnglishWithStork.Models

import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity

enum class ReviewQuestionType {
    MULTIPLE_CHOICE,
    GUESS_WORD,
    FILL_BLANK
}

data class ReviewQuestion(
    val vocabulary: VocabularyEntity,
    val type: ReviewQuestionType,

    // Text chính xuất hiện trên màn hình
    val prompt: String,

    // Đáp án đúng
    val answer: String,

    // Chỉ dùng cho dạng trắc nghiệm
    val options: List<String> = emptyList(),

    // Chỉ dùng cho dạng điền câu
    val wordCount: Int = 1
)
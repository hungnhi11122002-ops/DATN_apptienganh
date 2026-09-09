package com.example.EnglishWithStork.util

import com.example.EnglishWithStork.Models.ReviewQuestion
import com.example.EnglishWithStork.Models.ReviewQuestionType
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity

object ReviewQuestionFactory {

    fun createSession(
        vocabularies: List<VocabularyEntity>,
        sessionSize: Int = 10
    ): List<ReviewQuestion> {

        if (vocabularies.isEmpty()) {
            return emptyList()
        }

        val questionBank =
            vocabularies.flatMap { vocabulary ->

                val questions =
                    mutableListOf<ReviewQuestion>()

                createMultipleChoice(
                    vocabulary = vocabulary,
                    allWords = vocabularies
                )?.let {
                    questions.add(it)
                }

                questions.add(
                    createGuessWord(vocabulary)
                )

                createFillBlank(vocabulary)
                    ?.let {
                        questions.add(it)
                    }

                questions
            }

        return questionBank
            .shuffled()
            .take(sessionSize.coerceAtMost(questionBank.size))
    }


    /*
     * DẠNG 1
     * English -> Vietnamese
     *
     * dog
     * A. con mèo
     * B. con chó
     * C. con bò
     * D. con vịt
     */
    private fun createMultipleChoice(
        vocabulary: VocabularyEntity,
        allWords: List<VocabularyEntity>
    ): ReviewQuestion? {

        val distractors =
            allWords
                .filter {
                    it.id != vocabulary.id
                }
                .map {
                    it.vietnamese
                }
                .distinct()
                .shuffled()
                .take(3)

        if (distractors.size < 3) {
            return null
        }

        val options =
            (distractors + vocabulary.vietnamese)
                .shuffled()

        return ReviewQuestion(
            vocabulary = vocabulary,
            type = ReviewQuestionType.MULTIPLE_CHOICE,
            prompt =
                "Từ \"${vocabulary.english}\" có nghĩa là gì?",
            answer = vocabulary.vietnamese,
            options = options
        )
    }


    /*
     * DẠNG 2
     * Vietnamese -> English
     */
    private fun createGuessWord(
        vocabulary: VocabularyEntity
    ): ReviewQuestion {

        return ReviewQuestion(
            vocabulary = vocabulary,
            type = ReviewQuestionType.GUESS_WORD,
            prompt = vocabulary.vietnamese,
            answer = vocabulary.english
        )
    }


    /*
     * DẠNG 3
     * example_en -> hide vocabulary
     */
    private fun createFillBlank(
        vocabulary: VocabularyEntity
    ): ReviewQuestion? {

        val example =
            vocabulary.exampleEnglish
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: return null

        val cloze =
            createClozeSentence(
                example = example,
                vocabulary = vocabulary.english
            ) ?: return null

        return ReviewQuestion(
            vocabulary = vocabulary,
            type = ReviewQuestionType.FILL_BLANK,
            prompt = cloze.sentence,
            answer = cloze.answer,
            wordCount =
                cloze.answer
                    .trim()
                    .split(Regex("\\s+"))
                    .size
        )
    }


    private fun createClozeSentence(
        example: String,
        vocabulary: String
    ): ClozeResult? {

        val candidates =
            buildCandidates(vocabulary)

        candidates.forEach { candidate ->

            val regex =
                Regex(
                    "\\b${Regex.escape(candidate)}\\b",
                    RegexOption.IGNORE_CASE
                )

            val match =
                regex.find(example)

            if (match != null) {

                val actualAnswer =
                    match.value

                val sentence =
                    example.replaceRange(
                        match.range,
                        "______"
                    )

                return ClozeResult(
                    sentence = sentence,
                    answer = actualAnswer
                )
            }
        }

        return null
    }


    /*
     * Một số example sử dụng dạng số nhiều:
     *
     * blueberry -> blueberries
     * raspberry -> raspberries
     * plum -> plums
     *
     * nên không chỉ tìm đúng nguyên mẫu.
     */
    private fun buildCandidates(
        word: String
    ): List<String> {

        val clean =
            word.trim()

        val result =
            linkedSetOf<String>()

        result.add(clean)

        if (!clean.contains(" ")) {

            result.add("${clean}s")
            result.add("${clean}es")

            if (
                clean.endsWith(
                    "y",
                    ignoreCase = true
                ) &&
                clean.length > 1
            ) {
                result.add(
                    clean.dropLast(1) + "ies"
                )
            }

            if (
                clean.endsWith(
                    "e",
                    ignoreCase = true
                )
            ) {
                result.add(
                    clean + "d"
                )

                result.add(
                    clean.dropLast(1) + "ing"
                )
            } else {

                result.add(
                    clean + "ed"
                )

                result.add(
                    clean + "ing"
                )
            }
        }

        return result
            .sortedByDescending {
                it.length
            }
    }


    private data class ClozeResult(
        val sentence: String,
        val answer: String
    )
}
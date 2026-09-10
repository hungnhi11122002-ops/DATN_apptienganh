package com.example.EnglishWithStork.activity_trangchu

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.EnglishWithStork.Models.ReviewQuestion
import com.example.EnglishWithStork.Models.ReviewQuestionType
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.databinding.FragmentOnTapBinding
import com.example.EnglishWithStork.databinding.LayoutReviewFillBlankBinding
import com.example.EnglishWithStork.databinding.LayoutReviewGuessWordBinding
import com.example.EnglishWithStork.databinding.LayoutReviewMultipleChoiceBinding
import com.example.EnglishWithStork.util.ReviewQuestionFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class OnTapFragment : Fragment() {

    private var _binding: FragmentOnTapBinding? = null

    private val binding:
            FragmentOnTapBinding
        get() = _binding!!

    private lateinit var database:
            AppDatabase

    private var topicId: Int = 0
    private var topicName: String = ""
    private var mode: String = MODE_REVIEW

    private var score = 0
    private var correctCount = 0
    private var hearts = MAX_HEARTS
    private var isTestFinished = false

    private val isTestMode: Boolean
        get() = mode == MODE_TEST

    private var vocabularies:
            List<VocabularyEntity> =
        emptyList()

    private var questions:
            List<ReviewQuestion> =
        emptyList()

    private var currentIndex = 0

    /*
     * Dùng cho dạng nhập từ.
     *
     * false:
     * nút = Kiểm tra
     *
     * true:
     * nút = Câu tiếp theo
     */
    private var isAnswered = false


    private var multipleChoiceBinding:
            LayoutReviewMultipleChoiceBinding? =
        null

    private var guessWordBinding:
            LayoutReviewGuessWordBinding? =
        null

    private var fillBlankBinding:
            LayoutReviewFillBlankBinding? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicId = arguments?.getInt(ARG_TOPIC_ID) ?: 0

        topicName = arguments?.getString(
            ARG_TOPIC_NAME
        ).orEmpty()

        mode = arguments?.getString(ARG_MODE) ?: MODE_REVIEW
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentOnTapBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        database = AppDatabase.getDatabase(
            requireContext()
        )

        setupModeUi()
        setupListeners()
        loadVocabulary()
    }

    private fun setupModeUi() {

        if (isTestMode) {

            binding.tvTitle.text = "Kiểm tra"
            binding.layoutTestStatus.visibility = View.VISIBLE

            updateTestStatus()

        } else {

            binding.tvTitle.text = "Ôn tập"
            binding.layoutTestStatus.visibility = View.GONE
        }
    }

    private fun updateTestStatus() {

        if (!isTestMode) {
            return
        }

        binding.tvScore.text = "Điểm: $score"

        binding.tvHearts.text = when (hearts) {
            3 -> "♥ ♥ ♥"
            2 -> "♥ ♥ ♡"
            1 -> "♥ ♡ ♡"
            else -> "♡ ♡ ♡"
        }
    }

    private fun setupListeners() {

        binding.btnBack
            .setOnClickListener {

                parentFragmentManager
                    .popBackStack()
            }

        binding.btnPrimary
            .setOnClickListener {

                handlePrimaryButton()
            }
    }


    /*
     * Lấy đúng vocabulary của topic user vừa chọn.
     *
     * Không sử dụng getReviewWords().
     */
    private fun loadVocabulary() {

        if (topicId <= 0) {

            Toast.makeText(
                requireContext(),
                "Không tìm thấy chủ đề",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                vocabularies =
                    database
                        .vocabularyDao()
                        .observeWordsByTopic(
                            topicId
                        )
                        .first()

                if (vocabularies.isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Chủ đề này chưa có từ vựng",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                startNewSession()
            }
    }


    /*
     * Tạo lượt ôn mới.
     *
     * Question bank:
     * mỗi vocabulary có 3 dạng.
     *
     * Sau đó random 10 câu.
     */
    private fun startNewSession() {

        questions =
            ReviewQuestionFactory
                .createSession(
                    vocabularies = vocabularies,
                    sessionSize = SESSION_SIZE
                )

        currentIndex = 0

        if (isTestMode) {
            score = 0
            correctCount = 0
            hearts = MAX_HEARTS
            isTestFinished = false

            updateTestStatus()
        }

        showCurrentQuestion()
    }

    private fun recordTestAnswer(
        isCorrect: Boolean
    ): Boolean {

        // Ôn tập không tính điểm hoặc tim.
        if (!isTestMode) {
            return false
        }

        // Nếu bài kiểm tra đã kết thúc thì không xử lý thêm.
        if (isTestFinished) {
            return true
        }

        if (isCorrect) {
            correctCount++
        } else {
            hearts = (hearts - 1)
                .coerceAtLeast(0)
        }

        // Điểm luôn tính trên tổng số câu của bài kiểm tra.
        score =
            if (questions.isNotEmpty()) {
                ((correctCount.toFloat() / questions.size) * 100)
                    .toInt()
            } else {
                0
            }

        updateTestStatus()

        // Hết tim: dừng bài kiểm tra ngay và tổng kết điểm.
        if (hearts <= 0) {
            isTestFinished = true

            showTestFinishedDialog(
                outOfHearts = true
            )

            return true
        }

        return false
    }


    private fun showCurrentQuestion() {

        // Không được hiển thị câu mới nếu bài kiểm tra đã kết thúc.
        if (isTestMode && isTestFinished) {
            return
        }

        if (questions.isEmpty()) {
            return
        }

        if (currentIndex >
            questions.lastIndex
        ) {

            showFinishedDialog()

            return
        }

        resetChildBindings()

        isAnswered = false

        val question =
            questions[currentIndex]

        binding.tvProgress.text =
            "${currentIndex + 1}/${questions.size}"

        when (question.type) {

            ReviewQuestionType
                .MULTIPLE_CHOICE -> {

                showMultipleChoice(
                    question
                )
            }

            ReviewQuestionType
                .GUESS_WORD -> {

                showGuessWord(
                    question
                )
            }

            ReviewQuestionType
                .FILL_BLANK -> {

                showFillBlank(
                    question
                )
            }
        }
    }


    // =============================
    // DẠNG 1: TRẮC NGHIỆM
    // =============================

    private fun showMultipleChoice(
        question: ReviewQuestion
    ) {

        binding.tvQuestionType.text =
            "▣  Trắc nghiệm"

        binding.btnPrimary.text =
            "Câu tiếp theo"

        binding.btnPrimary.isEnabled =
            false

        val childBinding =
            LayoutReviewMultipleChoiceBinding
                .inflate(
                    layoutInflater,
                    binding.questionContainer,
                    false
                )

        multipleChoiceBinding =
            childBinding

        binding.questionContainer
            .addView(
                childBinding.root
            )

        childBinding.tvQuestion.text =
            question.prompt

        val radios =
            listOf(
                childBinding.radioAnswer1,
                childBinding.radioAnswer2,
                childBinding.radioAnswer3,
                childBinding.radioAnswer4
            )

        val letters =
            listOf(
                "A",
                "B",
                "C",
                "D"
            )

        radios.forEachIndexed {
                index,
                radioButton ->

            val option =
                question.options
                    .getOrNull(index)

            if (option == null) {

                radioButton.visibility =
                    View.GONE

            } else {

                radioButton.visibility =
                    View.VISIBLE

                radioButton.text =
                    "${letters[index]}. $option"

                radioButton.setOnClickListener {

                    checkMultipleChoice(
                        question = question,
                        selectedIndex = index,
                        radios = radios
                    )
                }
            }
        }
    }


    private fun checkMultipleChoice(
        question: ReviewQuestion,
        selectedIndex: Int,
        radios: List<RadioButton>
    ) {

        if (isAnswered) {
            return
        }

        isAnswered = true

        // Khóa đáp án ngay sau khi user chọn.
        radios.forEach {
            it.isEnabled = false
        }

        val selectedAnswer =
            question.options[
                selectedIndex
            ]

        val isCorrect =
            selectedAnswer == question.answer

        val testEnded =
            recordTestAnswer(isCorrect)

        // Nếu vừa mất tim cuối cùng thì dialog tổng kết đã được mở.
        // Không cho xử lý tiếp hoặc sang câu kế tiếp.
        if (testEnded) {
            binding.btnPrimary.isEnabled = false
            return
        }

        val childBinding =
            multipleChoiceBinding
                ?: return

        childBinding.tvFeedback
            .visibility =
            View.VISIBLE

        if (isCorrect) {

            childBinding.tvFeedback.text =
                "Chính xác!"

            childBinding.tvFeedback
                .setTextColor(
                    Color.parseColor(
                        "#2E7D32"
                    )
                )

        } else {

            childBinding.tvFeedback.text =
                "Đáp án đúng: ${question.answer}"

            childBinding.tvFeedback
                .setTextColor(
                    Color.parseColor(
                        "#C62828"
                    )
                )
        }

        binding.btnPrimary.isEnabled =
            true
    }


    // =============================
    // DẠNG 2: ĐOÁN TỪ
    // =============================

    private fun showGuessWord(
        question: ReviewQuestion
    ) {

        binding.tvQuestionType.text =
            "▣  Đoán từ"

        binding.btnPrimary.text =
            "Kiểm tra"

        binding.btnPrimary.isEnabled =
            true

        val childBinding =
            LayoutReviewGuessWordBinding
                .inflate(
                    layoutInflater,
                    binding.questionContainer,
                    false
                )

        guessWordBinding =
            childBinding

        binding.questionContainer
            .addView(
                childBinding.root
            )

        /*
         * prompt lúc này chính là
         * nghĩa tiếng Việt.
         */
        childBinding.tvMeaning.text =
            question.prompt

        childBinding.btnHint
            .setOnClickListener {

                val phonetic =
                    question
                        .vocabulary
                        .phonetic
                        ?.trim()
                        .orEmpty()

                childBinding
                    .tvExtraHint
                    .visibility =
                    View.VISIBLE

                childBinding
                    .tvExtraHint
                    .text =
                    if (
                        phonetic.isNotBlank()
                    ) {

                        "Phiên âm: $phonetic"

                    } else {

                        val firstLetter =
                            question.answer
                                .firstOrNull()
                                ?.uppercaseChar()
                                ?: '?'

                        "Từ bắt đầu bằng chữ $firstLetter"
                    }
            }
    }


    // =============================
    // DẠNG 3: ĐIỀN TỪ
    // =============================

    private fun showFillBlank(
        question: ReviewQuestion
    ) {

        binding.tvQuestionType.text =
            "✎  Điền từ"

        binding.btnPrimary.text =
            "Kiểm tra"

        binding.btnPrimary.isEnabled =
            true

        val childBinding =
            LayoutReviewFillBlankBinding
                .inflate(
                    layoutInflater,
                    binding.questionContainer,
                    false
                )

        fillBlankBinding =
            childBinding

        binding.questionContainer
            .addView(
                childBinding.root
            )

        childBinding.tvSentence.text =
            question.prompt

        childBinding.tvWordCount.text =
            "Nghĩa cần điền: ${question.vocabulary.vietnamese} • ${question.wordCount} từ"
    }


    /*
     * Nút dưới màn hình.
     */
    private fun handlePrimaryButton() {

        if (questions.isEmpty()) {
            return
        }

        // Bài kiểm tra đã kết thúc thì không xử lý nút nữa.
        if (isTestMode && isTestFinished) {
            return
        }

        val question =
            questions[currentIndex]

        when (question.type) {

            ReviewQuestionType
                .MULTIPLE_CHOICE -> {

                if (!isAnswered) {

                    Toast.makeText(
                        requireContext(),
                        "Hãy chọn một đáp án",
                        Toast.LENGTH_SHORT
                    ).show()

                    return
                }

                goToNextQuestion()
            }


            ReviewQuestionType
                .GUESS_WORD -> {

                if (!isAnswered) {

                    checkGuessWord(
                        question
                    )

                } else {

                    goToNextQuestion()
                }
            }


            ReviewQuestionType
                .FILL_BLANK -> {

                if (!isAnswered) {

                    checkFillBlank(
                        question
                    )

                } else {

                    goToNextQuestion()
                }
            }
        }
    }


    private fun checkGuessWord(
        question: ReviewQuestion
    ) {
        val childBinding =
            guessWordBinding
                ?: return

        val userAnswer =
            childBinding
                .edtAnswer
                .text
                ?.toString()
                .orEmpty()

        if (userAnswer.isBlank()) {

            childBinding
                .inputLayoutAnswer
                .error =
                "Bạn chưa nhập đáp án"

            return
        }

        childBinding
            .inputLayoutAnswer
            .error = null

        val isCorrect =
            normalizeAnswer(
                userAnswer
            ) ==
                    normalizeAnswer(
                        question.answer
                    )

        // Đánh dấu câu hiện tại đã được trả lời trước khi chấm tim.
        isAnswered = true
        childBinding.edtAnswer.isEnabled = false
        hideKeyboard()

        val testEnded =
            recordTestAnswer(isCorrect)

        // Hết tim thì dừng ngay tại đây.
        if (testEnded) {
            binding.btnPrimary.isEnabled = false
            return
        }

        showTextResult(
            correct = isCorrect,
            correctAnswer =
                question.answer,
            feedbackView =
                childBinding.tvFeedback
        )

        finishChecking()
    }


    private fun checkFillBlank(
        question: ReviewQuestion
    ) {

        val childBinding =
            fillBlankBinding
                ?: return

        val userAnswer =
            childBinding
                .edtAnswer
                .text
                ?.toString()
                .orEmpty()

        if (userAnswer.isBlank()) {

            childBinding
                .inputLayoutAnswer
                .error =
                "Bạn chưa nhập đáp án"

            return
        }

        childBinding
            .inputLayoutAnswer
            .error = null

        val isCorrect =
            normalizeAnswer(
                userAnswer
            ) ==
                    normalizeAnswer(
                        question.answer
                    )

        // Đánh dấu câu hiện tại đã được trả lời trước khi chấm tim.
        isAnswered = true
        childBinding.edtAnswer.isEnabled = false
        hideKeyboard()

        val testEnded =
            recordTestAnswer(isCorrect)

        // Hết tim thì dừng ngay tại đây.
        if (testEnded) {
            binding.btnPrimary.isEnabled = false
            return
        }

        showTextResult(
            correct = isCorrect,
            correctAnswer =
                question.answer,
            feedbackView =
                childBinding.tvFeedback
        )

        finishChecking()
    }


    private fun showTextResult(
        correct: Boolean,
        correctAnswer: String,
        feedbackView:
        android.widget.TextView
    ) {

        feedbackView.visibility =
            View.VISIBLE

        if (correct) {

            feedbackView.text =
                "Chính xác!"

            feedbackView
                .setTextColor(
                    Color.parseColor(
                        "#2E7D32"
                    )
                )

        } else {

            feedbackView.text =
                "Đáp án đúng: $correctAnswer"

            feedbackView
                .setTextColor(
                    Color.parseColor(
                        "#C62828"
                    )
                )
        }
    }


    private fun finishChecking() {

        isAnswered = true

        hideKeyboard()

        binding.btnPrimary.text =
            "Câu tiếp theo"
    }


    private fun goToNextQuestion() {

        // Phòng trường hợp nút vẫn nhận click sau khi bài kiểm tra đã kết thúc.
        if (isTestMode && (isTestFinished || hearts <= 0)) {
            return
        }

        currentIndex++

        showCurrentQuestion()
    }


    /*
     * Ôn tập chỉ thông báo hoàn thành.
     * Kiểm tra sẽ tổng kết điểm và số tim còn lại.
     */
    private fun showFinishedDialog() {

        if (isTestMode) {
            showTestFinishedDialog()
            return
        }

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Hoàn thành ôn tập")
            .setMessage(
                "Bạn đã hoàn thành lượt ôn tập chủ đề \"$topicName\"."
            )
            .setPositiveButton(
                "Ôn lại"
            ) { _, _ ->

                startNewSession()
            }
            .setNegativeButton(
                "Chọn chủ đề"
            ) { _, _ ->

                parentFragmentManager
                    .popBackStack()
            }
            .show()
    }


    private fun showTestFinishedDialog(
        outOfHearts: Boolean = false
    ) {

        if (!isTestMode) {
            return
        }

        // Khóa hoàn toàn bài kiểm tra trước khi mở dialog.
        isTestFinished = true
        binding.btnPrimary.isEnabled = false
        hideKeyboard()

        val totalQuestions = questions.size

        val answeredQuestions =
            if (outOfHearts) {
                (currentIndex + 1)
                    .coerceAtMost(totalQuestions)
            } else {
                totalQuestions
            }

        val wrongCount =
            (answeredQuestions - correctCount)
                .coerceAtLeast(0)

        val title =
            if (outOfHearts) {
                "Hết tim"
            } else {
                "Hoàn thành kiểm tra"
            }

        val message =
            if (outOfHearts) {
                """
                Bạn đã hết 3 tim nên bài kiểm tra kết thúc.

                Chủ đề: $topicName

                Điểm: $score/100
                Số câu đã làm: $answeredQuestions/$totalQuestions
                Số câu đúng: $correctCount
                Số câu sai: $wrongCount
                Tim còn lại: 0/$MAX_HEARTS
                """.trimIndent()
            } else {
                """
                Bạn đã hoàn thành bài kiểm tra.

                Chủ đề: $topicName

                Điểm: $score/100
                Số câu đã làm: $totalQuestions/$totalQuestions
                Số câu đúng: $correctCount/$totalQuestions
                Số câu sai: $wrongCount
                Tim còn lại: $hearts/$MAX_HEARTS
                """.trimIndent()
            }

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Làm lại"
            ) { _, _ ->

                startNewSession()
            }
            .setNegativeButton(
                "Chọn chủ đề"
            ) { _, _ ->

                parentFragmentManager
                    .popBackStack()
            }
            // Không cho bấm ra ngoài dialog hoặc Back để quay lại bài đã hết tim.
            .setCancelable(false)
            .show()
    }


    private fun normalizeAnswer(
        text: String
    ): String {

        return text
            .trim()
            .lowercase(
                Locale.ENGLISH
            )
            .replace(
                Regex("\\s+"),
                " "
            )
    }


    private fun hideKeyboard() {

        val inputMethodManager =
            requireContext()
                .getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

        inputMethodManager
            .hideSoftInputFromWindow(
                view?.windowToken,
                0
            )
    }


    private fun resetChildBindings() {

        binding.questionContainer
            .removeAllViews()

        multipleChoiceBinding = null
        guessWordBinding = null
        fillBlankBinding = null
    }


    override fun onDestroyView() {

        multipleChoiceBinding = null
        guessWordBinding = null
        fillBlankBinding = null

        _binding = null

        super.onDestroyView()
    }


    companion object {

        private const val SESSION_SIZE = 10

        const val MODE_REVIEW = "review"
        const val MODE_TEST = "test"

        private const val MAX_HEARTS = 3

        private const val ARG_TOPIC_ID = "topic_id"
        private const val ARG_TOPIC_NAME = "topic_name"
        private const val ARG_MODE = "mode"

        fun newInstance(
            topicId: Int,
            topicName: String,
            mode: String = MODE_REVIEW
        ): OnTapFragment {

            return OnTapFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TOPIC_ID, topicId)
                    putString(ARG_TOPIC_NAME, topicName)
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}

package com.example.EnglishWithStork.activity_trangchu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.EnglishWithStork.R
import com.example.EnglishWithStork.RoomDatabase.AppDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.databinding.FragmentFlashCardBinding
import com.example.EnglishWithStork.util.EnglishTtsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import coil3.load
import coil3.request.placeholder

class FlashCardFragment : Fragment() {

    private var _binding: FragmentFlashCardBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var ttsManager: EnglishTtsManager

    private var topicId: Int = -1
    private var topicName: String = ""

    private var vocabularies: List<VocabularyEntity> = emptyList()
    private var currentIndex = 0

    private var isFront = true
    private var isAnimating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicId = requireArguments().getInt(ARG_TOPIC_ID, -1)
        topicName = requireArguments().getString(ARG_TOPIC_NAME).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFlashCardBinding.inflate(
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
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getDatabase(requireContext())

        ttsManager = EnglishTtsManager(requireContext())

        setupListeners()

        observeVocabulary()
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnFlipFront.setOnClickListener {
            flipCard()
        }

        binding.btnFlipBack.setOnClickListener {
            flipCard()
        }

        binding.btnPrevious.setOnClickListener {
            showPreviousWord()
        }

        binding.btnNext.setOnClickListener {
            showNextWord()
        }

        binding.btnSpeak.setOnClickListener {
            speakCurrentWord()
        }

        binding.tvHint.setOnClickListener {
            speakCurrentWord()
        }
    }

    private fun observeVocabulary() {

        if (topicId <= 0) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                database
                    .vocabularyDao()
                    .observeWordsByTopic(topicId)
                    .collectLatest { list ->

                        vocabularies = list
                        currentIndex = 0

                        if (vocabularies.isNotEmpty()) {
                            showCurrentWord()
                        }
                    }
            }
        }
    }

    private fun showCurrentWord() {

        if (vocabularies.isEmpty()) {
            return
        }

        val vocabulary = vocabularies[currentIndex]

        // Luôn trở lại mặt trước khi đổi từ
        isFront = true

        binding.cardFront.isVisible = true
        binding.cardBack.isVisible = false

        // Mặt trước
        binding.tvEnglish.text = vocabulary.english
        binding.tvVietnamese.text = vocabulary.vietnamese

        binding.tvWordClass.text =
            vocabulary.wordClass ?: ""

        // Mặt sau
        binding.tvExampleEnglish.text =
            vocabulary.exampleEnglish
                ?: "Chưa có câu ví dụ."

        binding.tvExampleVietnamese.text =
            vocabulary.exampleVietnamese
                ?: "Chưa có bản dịch."

        // Tiến trình
        binding.tvProgress.text =
            "${currentIndex + 1}/${vocabularies.size}"

        loadVocabularyImage(vocabulary)
    }

    private fun showNextWord() {

        if (vocabularies.isEmpty()) {
            return
        }

        if (currentIndex < vocabularies.lastIndex) {
            currentIndex++
            showCurrentWord()
        }
    }

    private fun showPreviousWord() {

        if (vocabularies.isEmpty()) {
            return
        }

        if (currentIndex > 0) {
            currentIndex--
            showCurrentWord()
        }
    }

    private fun speakCurrentWord() {

        if (vocabularies.isEmpty()) {
            return
        }

        val vocabulary =
            vocabularies[currentIndex]

        ttsManager.speak(
            vocabulary.english
        )
    }

    private fun loadVocabularyImage(
        vocabulary: VocabularyEntity
    ) {

        val publicId = vocabulary.imageName

        if (publicId.isNullOrBlank()) {

            binding.imgWord.setImageResource(
                R.drawable.ic_flashcard
            )

            return
        }

        val imageUrl =
            "https://res.cloudinary.com/" +
                    "$CLOUDINARY_CLOUD_NAME/" +
                    "image/upload/" +
                    "c_limit,w_700,f_auto,q_auto/" +
                    publicId

        binding.imgWord.load(imageUrl) {

            placeholder(
                R.drawable.ic_flashcard
            )

            error(
                R.drawable.ic_flashcard
            )
        }
    }

    private fun flipCard() {

        if (isAnimating) {
            return
        }

        isAnimating = true

        val scale =
            resources.displayMetrics.density

        binding.cardContainer.cameraDistance =
            8000 * scale

        binding.cardContainer
            .animate()
            .rotationY(90f)
            .setDuration(180)
            .withEndAction {

                isFront = !isFront

                binding.cardFront.isVisible =
                    isFront

                binding.cardBack.isVisible =
                    !isFront

                binding.cardContainer.rotationY =
                    -90f

                binding.cardContainer
                    .animate()
                    .rotationY(0f)
                    .setDuration(180)
                    .withEndAction {
                        isAnimating = false
                    }
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {

        ttsManager.release()

        _binding = null

        super.onDestroyView()
    }

    companion object {

        private const val CLOUDINARY_CLOUD_NAME =
            "aus6sa62"

        private const val ARG_TOPIC_ID =
            "topic_id"

        private const val ARG_TOPIC_NAME =
            "topic_name"

        fun newInstance(
            topicId: Int,
            topicName: String
        ): FlashCardFragment {

            return FlashCardFragment().apply {

                arguments = Bundle().apply {

                    putInt(
                        ARG_TOPIC_ID,
                        topicId
                    )

                    putString(
                        ARG_TOPIC_NAME,
                        topicName
                    )
                }
            }
        }
    }
}
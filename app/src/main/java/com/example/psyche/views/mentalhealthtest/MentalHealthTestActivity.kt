package com.example.psyche.views.mentalhealthtest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psyche.data.ChatItem
import com.example.psyche.databinding.ActivityMentalHealthTestBinding
import com.example.psyche.views.adapters.ChatAdapter
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.ml.modeldownloader.DownloadType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MentalHealthTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMentalHealthTestBinding
    private val viewModel: MentalHealthTestViewModel by viewModels()
    private val chatItems = mutableListOf<ChatItem>()
    private val selectedAnswers = mutableMapOf<Int, String>()
    private lateinit var adapter: ChatAdapter
    private var interpreter: Interpreter? = null

    private val answerValues = mapOf(
        "Never" to 1,
        "Always" to 2,
        "Often" to 3,
        "Rarely" to 4,
        "Sometimes" to 5,
        "Not at all" to 6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentalHealthTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter(chatItems, selectedAnswers)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        showNextQuestion()

        // Firebase model download
        val conditions = CustomModelDownloadConditions.Builder()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("Mental-Health-Test", DownloadType.LOCAL_MODEL, conditions)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val modelFile = task.result?.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                    }
                } else {
                    // Handle the error
                }
            }
    }

    private fun showNextQuestion() {
        val question = viewModel.getCurrentQuestion()
        chatItems.add(ChatItem(question, true))
        chatItems.add(ChatItem("", false))
        adapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(chatItems.size - 1)
    }

    fun onAnswerSelected(answer: String) {
        val answerValue = answerValues[answer] ?: 0
        chatItems[chatItems.size - 1] = ChatItem(answer, false)
        adapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(chatItems.size - 1)
        if (viewModel.nextQuestion(answerValue.toString())) {
            showNextQuestion()
        } else {
            getResultFromModel()
        }
    }

    private fun evaluateAnswers(answer1: Int, answer2: Int?): String {
        return when {
            answer1 == 1 && (answer2 == null || answer2 == 1) -> "Good"
            answer1 == 5 || answer1 == 6 || (answer2 != null && (answer2 == 5 || answer2 == 6)) -> "Fair"
            answer1 == 2 || answer1 == 3 || (answer2 != null && (answer2 == 2 || answer2 == 3)) -> "Bad"
            answer1 == 2 || (answer2 != null && answer2 == 2) -> "Need Attention"
            else -> "Unknown"
        }
    }

    private fun getResultFromModel() {
        val input = viewModel.answers.map { it.toFloat() }.toFloatArray()
        interpreter?.let {
            val inputBuffer =
                TensorBuffer.createFixedSize(intArrayOf(1, input.size), DataType.FLOAT32)
            inputBuffer.loadArray(input)

            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 4), DataType.FLOAT32)

            it.run(inputBuffer.buffer, outputBuffer.buffer.rewind())

            val predictions = outputBuffer.floatArray

            val predictedIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1

            val labelToName = mapOf(
                0 to "No depression",
                1 to "Mild",
                2 to "Moderate",
                3 to "Severe"
            )

            val predictedClassName = labelToName[predictedIndex] ?: "Unknown"

            Log.d("MentalHealthTest", "Depression State: ${predictions[predictedIndex]}")
            Log.d("MentalHealthTest", "Hasil Prediksi: $predictedClassName")

            // Evaluate answers
            val sleep = evaluateAnswers(viewModel.answers[0].toInt(), viewModel.answers[8].toInt())
            val fatigue =
                evaluateAnswers(viewModel.answers[3].toInt(), viewModel.answers[13].toInt())
            val interest = evaluateAnswers(viewModel.answers[2].toInt(), null)
            val concentration = evaluateAnswers(viewModel.answers[5].toInt(), null)

            // Show progress bar and delay
            binding.progressBar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBar.visibility = View.GONE
                displayResult(sleep, fatigue, interest, concentration, predictedClassName)
            }, 3000)
        }
    }

    private fun displayResult(
        sleep: String,
        fatigue: String,
        interest: String,
        concentration: String,
        predictedClassName: String
    ) {
        val displayText = when (predictedClassName) {
            "No depression" -> "Happy"
            "Mild" -> "Feeling Blue"
            "Moderate" -> "Struggling"
            "Severe" -> "Overwhelmed"
            else -> "Unknown"
        }

        val resultText =
            "Sleep: $sleep\nFatigue: $fatigue\nInterest: $interest\nConcentration: $concentration\nPrediction: $displayText"
        chatItems.add(ChatItem(resultText, false, true))
        adapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(chatItems.size - 1)
    }
}
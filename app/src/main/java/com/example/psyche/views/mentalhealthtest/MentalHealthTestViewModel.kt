package com.example.psyche.views.mentalhealthtest

import androidx.lifecycle.ViewModel

class MentalHealthTestViewModel : ViewModel() {
    val questions = listOf(
        "Do you have sleep disturbances?",
        "Do you experience changes in appetite?",
        "Have you lost interest in activities?",
        "Do you feel fatigued or have low energy?",
        "Do you feel worthless or have excessive guilt?",
        "Do you have difficulty concentrating?",
        "Do you experience physical agitation?",
        "Do you have thoughts of self-harm or suicide?",
        "Do you have issues with sleeping?",
        "Do you feel aggressive?",
        "Do you experience panic attacks?",
        "Do you feel hopeless?",
        "Do you feel restless?",
        "Do you lack energy?"
    )

    var currentQuestionIndex = 0
    val answers = mutableListOf<String>()

    fun getCurrentQuestion(): String {
        return questions[currentQuestionIndex]
    }

    fun nextQuestion(answerValue: String): Boolean {
        answers.add(answerValue)
        currentQuestionIndex++
        return currentQuestionIndex < questions.size
    }
}
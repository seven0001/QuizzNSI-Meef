package com.example.newquizz

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newquizz.database.QuestionService
import com.example.newquizz.entity.Question
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PremiereActivity : AppCompatActivity() {

    private var currentIndex: Int = 1 // Indice de la question actuelle
    private lateinit var questionNumber: TextView // TextView pour afficher le numéro de la question
    private lateinit var questionText: TextView // TextView pour afficher le texte de la question
    private lateinit var scoreText: TextView // TextView pour afficher le score
    private var currentScore: Int = 0 // Score actuel du joueur
    private var isFirstAnswer: Boolean = true // Indique si c'est la première réponse à la question
    private var questions: MutableList<Question> = mutableListOf() // Liste des questions
    private lateinit var answersButtons: Array<Button> // Tableau des boutons de réponse

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premiere)

        // Initialisation des éléments de l'interface utilisateur
        questionNumber = findViewById<TextView>(R.id.premiere_question_number)
        questionText = findViewById<TextView>(R.id.premiere_question_text)
        scoreText = findViewById<TextView>(R.id.premiere_score)

        // Initialisation des boutons de réponse
        answersButtons = arrayOf(
            findViewById<Button>(R.id.premiere_answer1),
            findViewById<Button>(R.id.premiere_answer2),
            findViewById<Button>(R.id.premiere_answer3),
            findViewById<Button>(R.id.premiere_answer4)
        )

        // Chargement des questions depuis la base de données
        GlobalScope.launch {
            loadQuestionsPremiere()
        }

        // Affichage du score initial
        displayScore()

        // Configuration des listeners pour les boutons de réponse
        answersButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                checkAnswer(index)
            }
        }
    }

    // Méthode pour afficher la question actuelle
    private fun displayQuestion() {
        val nbQuestions = questions.size // Nombre total de questions
        val currentQuestion = questions[currentIndex - 1] // Question actuelle
        questionNumber.setText("Question n°$currentIndex/$nbQuestions") // Affichage du numéro de la question
        questionText.text = currentQuestion.name // Affichage du texte de la question
        isFirstAnswer = true // Réinitialisation du drapeau pour la première réponse
        // Affichage des boutons de réponse avec les réponses correspondantes
        currentQuestion.answers.forEachIndexed { index, answer ->
            answersButtons[index].text = answer
            answersButtons[index].setBackgroundColor(Color.BLUE)
            answersButtons[index].isClickable = true
        }
    }

    // Méthode pour vérifier la réponse sélectionnée par l'utilisateur
    private fun checkAnswer(selectedAnswerIndex: Int) {
        val currentQuestion = questions[currentIndex - 1] // Question actuelle
        val selectedAnswer = currentQuestion.answers[selectedAnswerIndex] // Réponse sélectionnée

        val goodAnswer = currentQuestion.goodAnswer // Bonne réponse pour la question actuelle
        if (selectedAnswer == goodAnswer) { // Si la réponse sélectionnée est correcte
            val selectedButton = answersButtons[selectedAnswerIndex]
            // Coloration des boutons de réponse en fonction de la réponse sélectionnée
            answersButtons.forEachIndexed { index, button ->
                if (button === selectedButton) {
                    selectedButton.setBackgroundColor(Color.GREEN)
                } else {
                    button.setBackgroundColor(Color.RED)
                }
            }
            toastGoodAnswer() // Affichage d'un message pour la bonne réponse
            if (isFirstAnswer) { // Si c'est la première réponse à cette question
                currentScore++ // Incrémentation du score
                displayScore() // Mise à jour de l'affichage du score
            }
            Handler().postDelayed({
                currentIndex++ // Passage à la question suivante
                if (currentIndex - 1 < questions.size) { // Vérification s'il reste des questions
                    displayQuestion() // Affichage de la prochaine question
                } else {
                    finish() // Fin de l'activité une fois toutes les questions répondues
                }
            }, 3000) // Délai avant de passer à la question suivante
        } else { // Si la réponse sélectionnée est incorrecte
            answersButtons[selectedAnswerIndex].isClickable = false // Désactivation du bouton de réponse
            answersButtons[selectedAnswerIndex].setBackgroundColor(Color.RED) // Coloration en rouge
            toastBadAnswer() // Affichage d'un message pour la mauvaise réponse
            isFirstAnswer = false // Indiquer que ce n'est plus la première réponse à cette question
        }
    }

    // Méthode pour afficher un message pour une mauvaise réponse
    private fun toastBadAnswer() {
        val toastBad = Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT)
        toastBad.show()
    }

    // Méthode pour afficher un message pour une bonne réponse
    private fun toastGoodAnswer() {
        val toastGood = Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT)
        toastGood.show()
    }

    // Méthode pour charger les questions depuis la base de données
    private fun loadQuestionsPremiere() {
        val service = QuestionService()

        GlobalScope.launch {
            val allQuestions = service.getQuestions(1) // Récupération des questions de la catégorie 1

            if (allQuestions != null) {
                for (i in 0 until allQuestions.length()) { // Parcours de toutes les questions
                    val jsonObject = allQuestions.getJSONObject(i)
                    val answersJsonArray = jsonObject.getJSONArray("answers")
                    val answers = mutableListOf<String>()

                    for (j in 0 until answersJsonArray.length()) { // Parcours des réponses pour chaque question
                        answers.add(answersJsonArray.getString(j))
                    }

                    questions.add( // Ajout de la question à la liste
                        Question(
                            jsonObject.getString("name"),
                            answers,
                            jsonObject.getString("goodAnswer")
                        )
                    )
                }

                runOnUiThread {
                    if (questions.isNotEmpty()) { // S'il y a des questions chargées avec succès
                        displayQuestion() // Affichage de la première question
                    }
                }
            }
        }
    }

    // Méthode pour afficher le score actuel
    private fun displayScore() {
        scoreText.text = "Score : $currentScore/5" // Affichage du score
    }
}

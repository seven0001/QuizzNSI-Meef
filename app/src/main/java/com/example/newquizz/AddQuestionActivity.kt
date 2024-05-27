package com.example.newquizz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newquizz.database.QuestionService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class AddQuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_form)

        // Initialisation des éléments de la vue
        val questionInput = findViewById<EditText>(R.id.questionInput)
        val answer1Input = findViewById<EditText>(R.id.answer1Input)
        val answer2Input = findViewById<EditText>(R.id.answer2Input)
        val answer3Input = findViewById<EditText>(R.id.answer3Input)
        val answer4Input = findViewById<EditText>(R.id.answer4Input)
        val correctAnswerInput = findViewById<EditText>(R.id.correctAnswerInput)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val addQuestionButton = findViewById<Button>(R.id.addQuestionButton)

        // Configuration du Spinner à partir des ressources
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        // Définition de l'écouteur de clic pour le bouton d'ajout de question
        addQuestionButton.setOnClickListener {
            // Récupération des valeurs des champs
            val question = questionInput.text.toString()
            val answers = arrayOf(
                answer1Input.text.toString(),
                answer2Input.text.toString(),
                answer3Input.text.toString(),
                answer4Input.text.toString()
            )
            val correctAnswer = correctAnswerInput.text.toString()
            val category = categorySpinner.selectedItem.toString()

            // Vérification si la bonne réponse est parmi les réponses proposées
            if (correctAnswer in answers) {
                // Lancement d'une coroutine pour effectuer des opérations asynchrones
                GlobalScope.launch {
                    val service = QuestionService()
                    val categoryList = service.getCategory()

                    // Vérification si la liste des catégories est récupérée avec succès
                    if (categoryList != null) {
                        // Vérification si la catégorie sélectionnée est dans la liste
                        if (category in categoryList) {
                            val categoryId = categoryList[category]

                            // Création d'un objet JSON avec les données de la question
                            val data = JSONObject().apply {
                                put("name", question)
                                put("goodAnswer", correctAnswer)
                                put("answers", JSONArray(answers))
                                put("category", "api/categories/$categoryId")
                            }

                            // Envoi de la question au service et fin de l'activité
                            println("Data to send: $data")
                            service.createQuestion(data)
                            finish()
                        }
                    }
                }
            } else {
                // Affichage d'un message d'erreur si la bonne réponse n'est pas parmi les réponses proposées
                Toast.makeText(this, "La bonne réponse doit être parmi les réponses proposées.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

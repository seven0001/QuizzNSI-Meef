package com.example.newquizz.database

import org.json.JSONArray
import org.json.JSONObject

class QuestionService {

    // Méthode pour récupérer des questions aléatoires pour une catégorie donnée
    fun getQuestions(categoryId: Int): JSONArray? {
        try {
            // Création d'une instance de SendRequest avec l'URL de base
            val request = SendRequest("https://api.teamlynxsavinois.fr")
            // Envoi d'une requête GET pour obtenir les questions pour la catégorie spécifiée
            val response = request.sendGetRequest("/api/randomQuestions/$categoryId")
            // Conversion de la réponse JSON en un tableau JSON et retour
            return JSONArray(response)
        } catch (e: Exception) {
            // Gestion des erreurs en affichant la trace et le message d'erreur
            e.printStackTrace()
            println("Error: ${e.message}")
            return null
        }
    }

    // Méthode pour récupérer la liste des catégories avec leurs identifiants
    fun getCategory(): MutableMap<String, Int>? {
        try {
            // Création d'une carte mutable pour stocker les catégories et leurs identifiants
            val categoryMap: MutableMap<String, Int> = mutableMapOf()
            // Création d'une instance de SendRequest avec l'URL de base
            val request = SendRequest("https://api.teamlynxsavinois.fr")
            // Envoi d'une requête GET pour obtenir la liste des catégories
            val response = request.sendGetRequest("/api/categories")
            // Conversion de la réponse JSON en objet JSON
            val jsonObject = JSONObject(response)
            // Récupération de la liste des catégories dans le membre "hydra:member"
            val member = jsonObject.getJSONArray("hydra:member")

            // Parcours de chaque élément dans le membre "hydra:member"
            for (i in 0 until member.length()) {
                val memberObject = member.getJSONObject(i)
                // Extraction de l'identifiant et du nom de la catégorie
                val id = memberObject.getInt("id")
                val name = memberObject.getString("categoryName")
                // Ajout de la catégorie et de son identifiant à la carte
                categoryMap[name] = id
            }

            // Retour de la carte contenant les catégories et leurs identifiants
            return categoryMap
        } catch (e: Exception) {
            // Gestion des erreurs en affichant la trace et le message d'erreur
            e.printStackTrace()
            println("Error: ${e.message}")
            return null
        }
    }

    // Méthode pour créer une nouvelle question en envoyant les données JSON
    fun createQuestion(data: JSONObject) {
        try {
            // Création d'une instance de SendRequest avec l'URL de base
            val request = SendRequest("https://api.teamlynxsavinois.fr")
            // Envoi d'une requête POST avec les données JSON pour créer une nouvelle question
            val response = request.sendPostRequest("/api/questions", data)
            // Affichage de la réponse reçue après la création de la question
            println("Response: $response")
        } catch (e: Exception) {
            // Gestion des erreurs en affichant la trace et le message d'erreur
            e.printStackTrace()
            println("Error: ${e.message}")
        }
    }
}

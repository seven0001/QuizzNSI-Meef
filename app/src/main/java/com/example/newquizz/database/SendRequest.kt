package com.example.newquizz.database

import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SendRequest(private val baseUrl: String) {
    // Méthode pour envoyer une requête GET
    fun sendGetRequest(endpoint: String): String {
        // Création de l'URL de la requête en concaténant l'URL de base avec l'endpoint
        val urlObj = URL(baseUrl + endpoint)
        // Ouverture de la connexion HTTP
        val connection = urlObj.openConnection() as HttpURLConnection

        return try {
            // Configuration de la requête
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // Récupération du code de réponse HTTP
            val responseCode = connection.responseCode
            // Vérification si la requête est réussie (code 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lecture de la réponse et retour
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                // Lancement d'une exception en cas d'erreur HTTP
                throw RuntimeException("Failed : HTTP error code : $responseCode")
            }
        } catch (e: Exception) {
            // Lancement d'une exception en cas d'échec de la requête
            throw RuntimeException("Failed to send GET request: ${e.message}", e)
        } finally {
            // Déconnexion de la connexion HTTP
            connection.disconnect()
        }
    }

    // Méthode pour envoyer une requête POST avec un payload JSON
    fun sendPostRequest(endpoint: String, jsonPayload: JSONObject): String {
        // Création de l'URL de la requête en concaténant l'URL de base avec l'endpoint
        val urlObj = URL(baseUrl + endpoint)
        // Ouverture de la connexion HTTP
        val connection = urlObj.openConnection() as HttpURLConnection

        return try {
            // Configuration de la requête
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/ld+json; charset=UTF-8")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // Affichage des informations sur la requête
            println("Sending POST request to: $urlObj")
            println("Payload: $jsonPayload")

            // Écriture du payload JSON dans le flux de sortie de la connexion
            connection.outputStream.use { outputStream ->
                OutputStreamWriter(outputStream, "UTF-8").use { it.write(jsonPayload.toString()) }
            }

            // Récupération du code de réponse HTTP
            val responseCode = connection.responseCode
            // Vérification si la requête est réussie (codes 200 ou 201)
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                // Lecture de la réponse et retour
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                // Lancement d'une exception en cas d'erreur HTTP
                throw RuntimeException("Failed : HTTP error code : $responseCode")
            }
        } catch (e: Exception) {
            // Lancement d'une exception en cas d'échec de la requête
            throw RuntimeException("Failed to send POST request: ${e.message}", e)
        } finally {
            // Déconnexion de la connexion HTTP
            connection.disconnect()
        }
    }
}

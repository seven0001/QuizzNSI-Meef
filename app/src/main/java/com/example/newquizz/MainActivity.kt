package com.example.newquizz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val premiere = findViewById<Button>(R.id.premiere)
        val terminale = findViewById<Button>(R.id.terminale)
        val ajout = findViewById<Button>(R.id.ajout)

        val premiereVue = Intent(this, PremiereActivity::class.java)
        val terminaleVue = Intent(this, TerminaleActivity::class.java)
        val ajoutVue = Intent(this, AddQuestionActivity::class.java)

        premiere.setOnClickListener {
            startActivity(premiereVue)
        }

        terminale.setOnClickListener {
            startActivity(terminaleVue)
        }

        ajout.setOnClickListener {
            startActivity(ajoutVue)
        }
    }
}
package com.alterpat.squares

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game_over.*

class GameOverActivity : AppCompatActivity() {

    private var gameMode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        // get saved best score
        val sharedPref = getSharedPreferences(
            "tapItPrefs", Context.MODE_PRIVATE
        )

        var bestScore = 0

        // get score from last activity
        val score = intent.getIntExtra("score", 0)
        gameMode = intent.getStringExtra("gameMode")!!

        when(gameMode){
            "reflex" -> {bestScore = sharedPref.getInt("bestScoreReflex", 0); modeTitle.text = resources.getText(R.string.reflex_mode)}
            "memory" -> {bestScore = sharedPref.getInt("bestScoreMemory", 0); modeTitle.text =  resources.getText(R.string.memory_mode) }
        }

        // show score
        scoreText.text = score.toString()

        // show best score
        if(score > bestScore){
            bestScoreText.text = "NEW BEST!"
            // save new best to shredPref
            saveBestScore(sharedPref, score)

        }else
            bestScoreText.text = "BEST $bestScore"



        replayBtn.setOnClickListener {
            //startActivity(Intent(this, GameActivity::class.java))
            finish()

        }

    }

    private fun saveBestScore(sharedPref: SharedPreferences, bestScore : Int){
        var field = "bestScoreMemory"
        if(gameMode == "reflex")
            field = "bestScoreReflex"

        with(sharedPref.edit()){
            putInt(field, bestScore)
            apply()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
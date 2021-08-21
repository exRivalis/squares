package com.alterpat.squares

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.alterpat.squares.adapter.ItemClickListener
import com.alterpat.squares.adapter.SquareAdapter
import com.alterpat.squares.model.Square
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.Exception
import kotlin.random.Random

class GameReflexActivity : AppCompatActivity(),
    ItemClickListener {

    private lateinit var squares : ArrayList<Square>
    private var nbSquares : Int = 3
    private var squareSize : Int = 0
    private var lastPosition : Int = 0
    private var score: Int = 0
    private val startingRefreshRate : Long = 900
    private var refreshRate : Long = startingRefreshRate
    private val TRAP_RATE = 8

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var gameOverRunnable: Runnable

    private var gameOver: Boolean = false
    private var clicked : Boolean = true
    private var escapedTrap : Boolean = false


    private lateinit var mAdapter : SquareAdapter

    private lateinit var greenBeep : MediaPlayer
    private lateinit var redBeep : MediaPlayer
    private var isSoundOn = true;
    private var allowTraps = false
    private var hideTipsReflex = false
    private lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val screenWidth = resources.displayMetrics.widthPixels


        greenBeep = MediaPlayer.create(this, R.raw.tap_it_green_beep)
        greenBeep.setVolume(.3f, .3f)
        redBeep = MediaPlayer.create(this, R.raw.tap_it_red_beep)
        redBeep.setVolume(.3f, .3f)

        // get nb squares
        sharedPref = getSharedPreferences(
            "tapItPrefs", Context.MODE_PRIVATE
        )

        isSoundOn = sharedPref.getBoolean("isSoundOn", true)
        nbSquares = sharedPref.getInt("nbSquares", 3)
        allowTraps = sharedPref.getBoolean("allowTraps", false)
        hideTipsReflex = sharedPref.getBoolean("hideTipsReflex", false)



        val padding = (0.4 * screenWidth / nbSquares).toInt()
        val spacing = padding / nbSquares / 3

        squares = ArrayList()
        squareSize = (screenWidth - 2 * padding - 2 * spacing * nbSquares)/ nbSquares

        for(i in 0 until nbSquares*nbSquares)
            squares.add(Square(squareSize))

        mAdapter = SquareAdapter(this, squares, this)
        var mLayoutManager : GridLayoutManager = GridLayoutManager(this, nbSquares)

        recyclerView.addItemDecoration(SpacesItemDecoration(spacing, nbSquares))

        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }

        handler = Handler()
        runnable = Runnable {
            kotlin.run {
                var position : Int
                var isTrapped = false

                do {
                    position = Random.nextInt(nbSquares * nbSquares)
                }while (position == lastPosition)

                // increment score if escaped trap
                if(escapedTrap){
                    escapedTrap = false
                    score++
                    scoreText.text = score.toString()
                }

                // in 15 % of cases trap player
                if(allowTraps && score > 6 && Random.nextInt(100) > 100 - TRAP_RATE) {
                    squares[position].setState(Square.State.RED)
                    isTrapped = true
                }else
                    squares[position].setState(Square.State.GREEN)

                squares[lastPosition].setState(Square.State.NEUTRAL)

                mAdapter.notifyItemChanged(lastPosition)
                mAdapter.notifyItemChanged(position)

                if(clicked && !gameOver) {
                    clicked = isTrapped
                    lastPosition = position

                    escapedTrap = isTrapped

                    refreshRate = if (refreshRate>700) refreshRate -10 else refreshRate
                    refreshRate = if (refreshRate <= 700) refreshRate -5 else refreshRate
                    //refreshRate = if (refreshRate in 700..900) refreshRate -8 else refreshRate
                    //refreshRate = if (refreshRate in 500..699) refreshRate -5 else refreshRate

                    handler.postDelayed(runnable, refreshRate)
                }else if(!gameOver) {
                    stopGame()
                    squares[lastPosition].setState(Square.State.RED)
                    mAdapter.notifyItemChanged(lastPosition)
                }else
                    gameOver
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if(hideTipsReflex)
            startGame()
        else
            showRuleDialog()
    }

    override fun onStop() {
        super.onStop()
        gameOver = true
        try {
            handler.removeCallbacks(gameOverRunnable)
        }catch (e: Exception){}
        handler.removeCallbacks(runnable)
    }

    private fun showRuleDialog(){
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.dialog_reflex_title))
            .setMessage(resources.getString(R.string.reflex_supporting_text))
            .setPositiveButton(resources.getString(R.string.dialog_understood)) { dialog, which ->
                startGame()
            }
            .setNeutralButton(resources.getString(R.string.dialog_dont_show)) { dialog, which ->
                with(sharedPref.edit()){
                    hideTipsReflex = true
                    putBoolean("hideTipsReflex", hideTipsReflex)
                    apply()
                }
                startGame()
            }
            .show()
    }

    private fun startGame(){
        gameOver = false
        clicked = true

        score = 0
        scoreText.text = score.toString()
        refreshRate = startingRefreshRate

        squares.map { it.setState(Square.State.NEUTRAL) }
        mAdapter.notifyDataSetChanged()

        handler.postDelayed({
            kotlin.run {
                runnable.run()

            }
        }, startingRefreshRate)
    }

    private fun stopGame(){
        gameOver = true
        handler.removeCallbacks(runnable)

        if(isSoundOn)
            redBeep.start()

        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
        gameOverRunnable = Runnable {
            kotlin.run {
                val intent = Intent(this, GameOverActivity::class.java)
                intent.putExtra("score", score)
                intent.putExtra("gameMode", "reflex")
                startActivity(intent)
            }
        }

        handler.postDelayed(gameOverRunnable, 1000)

    }

    override fun onClick(item: Square, position: Int) {
        if(item.getState() == Square.State.GREEN && !clicked) {
            // update score
            if(isSoundOn){
                if(greenBeep.isPlaying) {
                    greenBeep.seekTo(0)
                }

                greenBeep.start()
            }

            score++
            scoreText.text = score.toString()
        }else if(!gameOver){
            stopGame()
            squares[position].setState(Square.State.RED)
            mAdapter.notifyDataSetChanged()
        }

        clicked = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gameOver = true
        try {
            handler.removeCallbacks(gameOverRunnable)
        }catch (e: Exception){}
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        greenBeep.release()
        redBeep.release()
    }

}
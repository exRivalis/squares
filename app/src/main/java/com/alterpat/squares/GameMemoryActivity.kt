package com.alterpat.squares

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
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
import java.lang.Exception
import kotlin.random.Random

class GameMemoryActivity : AppCompatActivity(),
    ItemClickListener {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var gameOverRunnable: Runnable
    private lateinit var squares : ArrayList<Square>
    private var cpt : Int = 0
    private var lastPosition : Int = -1
    private var nbSquares : Int = 3
    private var squareSize : Int = 0
    private var score: Int = 0
    private var stepLength : Long = 400
    private var interStepLength : Long = 200

    private lateinit var handler: Handler

    private var gameOver: Boolean = false

    private var currentStepIndex = 0


    private lateinit var mAdapter : SquareAdapter

    private lateinit var greenBeep : MediaPlayer
    private lateinit var redBeep : MediaPlayer
    private var isSoundOn = true
    private var hideTipsMemory = false
    private var isPreparingSteps = false
    private var memoryMode = "repeat"
    private lateinit var steps : ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val screenWidth = resources.displayMetrics.widthPixels

        steps = ArrayList()

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
        memoryMode = sharedPref.getString("memoryMode", "repeat")!!
        hideTipsMemory = sharedPref.getBoolean("hideTipsMemory", false)


        val padding = (0.35 * screenWidth / nbSquares).toInt()
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

    }

    /*
    * shows previous steps if any
    * adds new position to steps
     */

    private fun drawStep(stepIndex : Int) {
        if(!gameOver){
            // draw an existing step

            // if in random mode: regenerate random steps
            if(memoryMode == "random" && stepIndex == 0) {
                steps =
                    ArrayList(IntArray(steps.size) { Random.nextInt(nbSquares * nbSquares) }.asList())
                initPitch()
            }

            if(stepIndex < steps.size) {
                var index = steps[stepIndex]
                squares[index].setState(Square.State.GREEN)
                if (isSoundOn) {
                    greenBeep.seekTo(0)
                    updatePitch(stepIndex)
                    greenBeep.start()
                }
                mAdapter.notifyDataSetChanged()
                // delay clearing
                clearSquare(index)
                nextStep(stepIndex+1)


            }else {
                // add new step
                var position = Random.nextInt(nbSquares * nbSquares)
                if(position == lastPosition) cpt++

                while(position == lastPosition && cpt>1){
                    position = Random.nextInt(nbSquares * nbSquares)
                }

                if(position != lastPosition) cpt=0

                lastPosition = position

                squares[position].setState(Square.State.GREEN)
                if (isSoundOn) {
                    greenBeep.seekTo(0)

                    updatePitch(stepIndex)

                    greenBeep.start()
                }

                steps.add(position)
                mAdapter.notifyDataSetChanged()

                clearSquare(steps.last(), true)

            }
        }
    }

    private fun clearSquare(index : Int, isLast : Boolean = false){
        if(isLast) {
            currentStepIndex = 0
            isPreparingSteps = false
        }
        handler.postDelayed(Runnable {
            kotlin.run {
                squares[index].setState(Square.State.NEUTRAL)
                mAdapter.notifyItemChanged(index)
            }
        }, stepLength)

    }

    private fun nextStep(stepIndex: Int){
        handler.postDelayed(Runnable {
            kotlin.run {
                drawStep(stepIndex)
            }
        }, stepLength+interStepLength)
    }

    private fun addStep(stepIndex : Int){
        handler.postDelayed(Runnable {
            kotlin.run {
                squares[stepIndex].setState(Square.State.NEUTRAL)
                mAdapter.notifyItemChanged(stepIndex)
            }
        }, interStepLength)

        handler.postDelayed(Runnable {
            kotlin.run {
                isPreparingSteps = true
                drawStep(0)
            }
        }, stepLength+interStepLength)

    }

    override fun onStart() {
        super.onStart()
        if(hideTipsMemory)
            startGame()
        else
            showRuleDialog()
    }

    private fun startGame(){
        gameOver = false
        steps.clear()
        score = 0
        scoreText.text = "0"

        squares.map { it.setState(Square.State.NEUTRAL) }
        mAdapter.notifyDataSetChanged()

        handler.postDelayed({
            kotlin.run {
                isPreparingSteps = true
                drawStep(0)
            }
        }, stepLength)
    }

    private fun showRuleDialog(){
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.dialog_memory_title))
            .setMessage(resources.getString(R.string.memory_supporting_text))
            .setPositiveButton(resources.getString(R.string.dialog_understood)) { dialog, which ->
                startGame()
            }
            .setNeutralButton(resources.getString(R.string.dialog_dont_show)) { dialog, which ->
                with(sharedPref.edit()){
                    hideTipsMemory = true
                    putBoolean("hideTipsMemory", hideTipsMemory)
                    apply()
                }
                startGame()
            }
            .show()
    }

    private fun updatePitch(position : Int){
        return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var params = PlaybackParams()
            var pitch = 1 + position*0.05f
            if(pitch > 6f)
                pitch = 6f
            params.setPitch(pitch)
            greenBeep.playbackParams = params
        }
    }

    private fun initPitch(){
        return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var params = PlaybackParams()
            var pitch = 1f
            params.setPitch(pitch)
            greenBeep.playbackParams = params
        }
    }

    private fun stopGame(){
        gameOver = true

        if(isSoundOn)
            redBeep.start()

        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()

        gameOverRunnable = Runnable {
            kotlin.run {
                val intent = Intent(this, GameOverActivity::class.java)
                intent.putExtra("score", score)
                intent.putExtra("gameMode", "memory")
                startActivity(intent)
            }
        }
        handler.postDelayed(gameOverRunnable, 1000)

    }

    override fun onClick(item: Square, position: Int) {
        if (!gameOver && !isPreparingSteps)
            if(currentStepIndex == steps.size) {
                squares[position].setState(Square.State.RED)
                mAdapter.notifyDataSetChanged()
                stopGame()
            }

            else if(position == steps[currentStepIndex]){
                // the correct square
                if(currentStepIndex > 0)
                    squares[steps[currentStepIndex-1]].setState(Square.State.NEUTRAL)

                squares[position].setState(Square.State.GREEN)
                mAdapter.notifyDataSetChanged()

                if(isSoundOn){
                    if(greenBeep.isPlaying)
                        greenBeep.seekTo(0)

                    updatePitch(currentStepIndex)

                    greenBeep.start()
                }

                currentStepIndex++


                // is it the last element
                if(currentStepIndex == steps.size){
                    score++
                    scoreText.text = score.toString()

                    var index = steps[currentStepIndex-1]
                    addStep(index)
                    //stepper(0)
                    //stepperThread.run()

                }

            }else{
                squares[position].setState(Square.State.RED)
                mAdapter.notifyDataSetChanged()
                stopGame()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gameOver = true

        try{
            handler.removeCallbacks(gameOverRunnable)
        }catch (e: Exception){}
    }

    override fun onDestroy() {
        super.onDestroy()
        greenBeep.release()
        redBeep.release()
    }

    /**
     appuyez sur les carrées verts au fur et a mesure qu'il apparaissent

     Astuce:
     Touchez le carré vert avant qu'il ne change de place!

     A vos doigts, partez!

     Touchez les carrés verts dans l'ordre d'apparition
     */
}
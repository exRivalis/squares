package com.alterpat.squares

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.*
import com.alterpat.squares.adapter.SettingsAdapter
import com.alterpat.squares.adapter.SettingsItemClickListener
import com.alterpat.squares.model.SettingsItem
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SettingsItemClickListener {

    private var nbSquares : Int = 3
    private lateinit var mAdapter : SettingsAdapter
    private lateinit var items : ArrayList<SettingsItem>
    private lateinit var sharedPref : SharedPreferences

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get nb squares
        sharedPref = getSharedPreferences(
            "tapItPrefs", Context.MODE_PRIVATE
        )

        var isSoundOn = sharedPref.getBoolean("isSoundOn", true)

        soundSwitch.isChecked = isSoundOn

        nbSquares = sharedPref.getInt("nbSquares", 3)

        var allowTraps = sharedPref.getBoolean("allowTraps", false)
        var memoryMode = sharedPref.getString("memoryMode", "repeat")

        trapSwitch.isChecked = allowTraps

        if(memoryMode.equals("repeat")){
            repeatBtn.backgroundTintList = resources.getColorStateList(R.color.settingsColorAccent, theme)
            repeatText.setTextColor(resources.getColor(R.color.white, theme))
        }else{
            randomBtn.backgroundTintList = resources.getColorStateList(R.color.settingsColorAccent, theme)
            randomText.setTextColor(resources.getColor(R.color.white, theme))
        }

        items = ArrayList()
        var a = SettingsItem(0, R.drawable.transparent_bg)
        items.add(a!!)
        items.add(SettingsItem(2, R.drawable.bg_2x2))
        items.add(SettingsItem(3, R.drawable.bg_3x3))
        items.add(SettingsItem(4, R.drawable.bg_4x4))
        items.add(a!!)
        items.get(nbSquares-1).setSelected(true)

        val screenWidth = resources.displayMetrics.widthPixels

        var mLayoutManager : LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapter = SettingsAdapter(this, items, screenWidth, this)

        var snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(settingsRecyclerView)


        settingsRecyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }

        settingsRecyclerView.smoothScrollToPosition(nbSquares)

        saveBtn.setOnClickListener {
            with(sharedPref.edit()){
                putInt("nbSquares", nbSquares)
                putBoolean("allowTraps", trapSwitch.isChecked)
                putBoolean("isSoundOn", soundSwitch.isChecked)
                putString("memoryMode", memoryMode)
                apply()
            }
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        randomBtn.setOnClickListener{
            if(!memoryMode.equals("random")){
                randomBtn.backgroundTintList = resources.getColorStateList(R.color.settingsColorAccent, theme)
                randomText.setTextColor(resources.getColor(R.color.white, theme))

                memoryMode = "random"

                repeatBtn.backgroundTintList = resources.getColorStateList(R.color.white, theme)
                repeatText.setTextColor(resources.getColor(R.color.black, theme))
            }
        }

        repeatBtn.setOnClickListener{
            if(!memoryMode.equals("repeat")){
                repeatBtn.backgroundTintList = resources.getColorStateList(R.color.settingsColorAccent, theme)
                repeatText.setTextColor(resources.getColor(R.color.white, theme))

                memoryMode = "repeat"

                randomBtn.backgroundTintList = resources.getColorStateList(R.color.white, theme)
                randomText.setTextColor(resources.getColor(R.color.black, theme))
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(item: SettingsItem, position: Int) {
        if(item.getNbSquares() == 0){

        }
        else if(item.getNbSquares() != nbSquares){

            if(item.getNbSquares() > nbSquares)
                settingsRecyclerView.smoothScrollToPosition(position+1)
            else
                settingsRecyclerView.smoothScrollToPosition(position-1)

            items[nbSquares-1].setSelected(false)
            items[position].setSelected(true)
            nbSquares = item.getNbSquares()
            mAdapter.notifyDataSetChanged()

        }
    }
}
package com.alterpat.squares.model

class SettingsItem constructor(private var nbSquares: Int, private var resource : Int, private var selected : Boolean = false ){

    fun getNbSquares() : Int {
        return nbSquares
    }

    fun getTitle() : String {
        if (nbSquares > 0)
            return  "$nbSquares x ${this.nbSquares}"
        return ""
    }


    fun getResource() : Int {
        return resource
    }

    fun setSelected(selected: Boolean){
        this.selected = selected
    }

    fun isSelected() : Boolean {
        return selected
    }
}
package com.alterpat.squares.model

class Square constructor(private var size: Int){

    enum class State {
        GREEN, RED, NEUTRAL
    }

    private var state : State =
        State.NEUTRAL

    fun getState() : State {
        return state
    }

    fun setState(state : State) {
        this.state = state
    }

    fun setSize(size : Int){
        this.size = size
    }

    fun getSize() : Int {
        return size
    }

}
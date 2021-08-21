package com.alterpat.squares.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.alterpat.squares.R
import com.alterpat.squares.model.SettingsItem
import kotlinx.android.synthetic.main.settings_choice_layout.view.*

class SettingsAdapter (private val context : Context, private val items: ArrayList<SettingsItem>,
                       private var screenWidth : Int, private var clickListener: SettingsItemClickListener)
    : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun start(item : SettingsItem   , clickListener: SettingsItemClickListener) {
            itemView.setOnClickListener{
                clickListener.onClick(item, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_choice_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item : SettingsItem = items.get(position)


        var layoutParams : ViewGroup.LayoutParams = holder.itemView.grid.layoutParams
        var size = (screenWidth*0.4).toInt()
        layoutParams.height = size
        layoutParams.width = size

        holder.itemView.grid.layoutParams = layoutParams

        holder.start(item, clickListener)

        holder.itemView.grid.setImageResource(item.getResource())
        holder.itemView.title.text = item.getTitle()

        if (item.isSelected())
            holder.itemView.background = getDrawable(context, R.drawable.button_bg)
        else
            holder.itemView.background = getDrawable(context, R.drawable.transparent_bg)
    }

}

interface SettingsItemClickListener {
    fun onClick(item: SettingsItem, position: Int)
}
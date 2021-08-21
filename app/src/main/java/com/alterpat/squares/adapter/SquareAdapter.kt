package com.alterpat.squares.adapter
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.alterpat.squares.R
import com.alterpat.squares.model.Square


class SquareAdapter(private val context : Context, private val squares: ArrayList<Square>, var clickListener: ItemClickListener) : RecyclerView.Adapter<SquareAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun start(item : Square, clickListener: ItemClickListener) {
            itemView.setOnClickListener{
                clickListener.onClick(item, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.square_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return squares.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item : Square = squares.get(position)

        var layoutParams : ViewGroup.LayoutParams = holder.itemView.layoutParams
        layoutParams.height = item.getSize()
        layoutParams.width = item.getSize()

        holder.itemView.layoutParams = layoutParams

        holder.start(item, clickListener)

        when(item.getState()) {
            Square.State.NEUTRAL -> holder.itemView.findViewById<View>(
                R.id.content
            ).background = context.getDrawable(R.drawable.neutral_square)
            Square.State.GREEN -> holder.itemView.findViewById<View>(
                R.id.content
            ).background = context.getDrawable(R.drawable.green_square)
            Square.State.RED -> holder.itemView.findViewById<View>(
                R.id.content
            ).background = context.getDrawable(R.drawable.red_square)
        }
    }

}

interface ItemClickListener {
    fun onClick(item: Square, position: Int)
}
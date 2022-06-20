package com.example.monopolymanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.entities.Game
import com.google.firebase.firestore.FirebaseFirestore

private var PREF_NAME = "MONOPOLY"

class GameAdapter (private var games : MutableList<Game>?,
                   var onClick : (Int) -> Unit,
) : RecyclerView.Adapter<GameAdapter.GameHolder>()  {
    class GameHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun setGame(game: Game?) {
            val gameNameTxt : TextView = view.findViewById(R.id.gameNameTxt)
            gameNameTxt.text = game!!.name

            val playersListTxt : TextView = view.findViewById(R.id.playersList)
            var playersList = ""
            for (player in game.players) {
                playersList += "${player.player}, "
            }
            playersListTxt.text = playersList.substring(0, playersList.length - 2)
        }

        fun getCardView() : CardView {
            return view.findViewById(R.id.cardProperty)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game, parent,false)
        return (GameHolder(view))
    }

    override fun onBindViewHolder(holder: GameHolder, position: Int) {
        holder.setGame(games?.get(position))
        holder.getCardView().setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return games!!.size
    }

    fun remove(position: Int) {
        games?.removeAt(position)
    }
}
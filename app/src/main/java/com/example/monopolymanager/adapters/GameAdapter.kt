package com.example.monopolymanager.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.utils.OnSwipeTouchListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private var PREF_NAME = "MONOPOLY"

class GameAdapter (private var games : MutableList<Game>?,
                   var onClick : (Int) -> Unit,
) : RecyclerView.Adapter<GameAdapter.GameHolder>()  {
    class GameHolder(private var view: View, var context: Context) : RecyclerView.ViewHolder(view) {
        private val swipeLeft: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_left_anim)}
        private val swipeRight: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_right_anim)}
        
        fun setGame(game: Game?, onClick : (Int) -> Unit, index: Int) {
            val gameNameTxt : TextView = view.findViewById(R.id.gameNameTxt)
            gameNameTxt.text = game!!.name
            gameNameTxt.setOnClickListener {
                onClick(index)
            }

            val playersListTxt : TextView = view.findViewById(R.id.playersList)
            var playersList = ""
            for (player in game.players) {
                playersList += "${player.player}, "
            }
            playersListTxt.text = playersList.substring(0, playersList.length - 2)
            val cardGameDetails = view.findViewById<CardView>(R.id.cardGameDetails)

            val deleteBtn = view.findViewById<Button>(R.id.deleteBtn)
            deleteBtn.isEnabled = false

            deleteBtn.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.removeGame))
                    .setMessage(context.getString(R.string.removeGameExplanation))
                    .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        val username = sharedPref.getString("username", "")
                        game.players.removeIf { it.player == username }
                        if (game.players.isEmpty()) {
                            Firebase.firestore.collection("Game").document(game.name).delete()
                        } else {
                            game.properties.removeIf { it.idOwner == username }
                            Firebase.firestore.collection("Game").document(game.name).set(game)
                        }
                    }
                    .setNegativeButton(context.getString(R.string.cancel), /* listener = */ null)
                    .show()
            }

            val clickableLayout2 = view.findViewById<ConstraintLayout>(R.id.clickableLayout2)
            var isLeftOriented = true
            with(clickableLayout2) {
                setOnTouchListener(object: OnSwipeTouchListener(context) {
                    override fun onSwipeLeft() {
                        if (isLeftOriented) {
                            cardGameDetails.startAnimation(swipeLeft)
                            deleteBtn.isEnabled = true
                            isLeftOriented = false
                        }
                    }
                    override fun onSwipeRight() {
                        if (!isLeftOriented) {
                            cardGameDetails.startAnimation(swipeRight)
                            deleteBtn.isEnabled = false
                            isLeftOriented = true
                        }
                    }

                })
            }
        }

        fun getCardView() : CardView {
            return view.findViewById(R.id.cardGame)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game, parent,false)
        return (GameHolder(view, parent.context))
    }

    override fun onBindViewHolder(holder: GameHolder, position: Int) {
        holder.setGame(games?.get(position), onClick, position)
        holder.getCardView()
    }

    override fun getItemCount(): Int {
        return games!!.size
    }

    fun remove(position: Int) {
        games?.removeAt(position)
    }
}
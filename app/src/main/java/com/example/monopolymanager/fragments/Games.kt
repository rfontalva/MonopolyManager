package com.example.monopolymanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolymanager.R
import com.example.monopolymanager.adapters.GameAdapter
import com.example.monopolymanager.databinding.FragmentGamesBinding
import com.example.monopolymanager.viewmodels.GamesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Games : Fragment() {

    companion object {
        fun newInstance() = Games()
    }

    private lateinit var binding: FragmentGamesBinding
    private lateinit var viewModel: GamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGamesBinding.inflate(layoutInflater)
        viewModel = GamesViewModel(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        val customAlertDialog = buildNewGameDialog(scope)


        viewModel.isLoading.observe(viewLifecycleOwner) { stillLoading ->
            if (!stillLoading) {
                binding.gamesList.setHasFixedSize(true)
                binding.gamesList.layoutManager = LinearLayoutManager(context)
                binding.gamesList.adapter = GameAdapter(viewModel.getGames()){ index->
                    onItemClick(index)
                }
            }
        }

        viewModel.refreshGames.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                binding.gamesList.setHasFixedSize(true)
                binding.gamesList.layoutManager = LinearLayoutManager(context)
                binding.gamesList.adapter = GameAdapter(viewModel.getGames()){ index->
                    onItemClick(index)
                }
            }
        }

        binding.addGameBtn.setOnClickListener{
            customAlertDialog.show()
        }
    }

    fun onItemClick(position: Int) {
        viewModel.setGame(position)
        binding.root.findNavController().navigate(GamesDirections.actionGamesToHome2())
    }

    private fun buildNewGameDialog(scope: CoroutineScope) : AlertDialog {
        val newGame = layoutInflater.inflate(R.layout.new_game, null)
        val hasErrorTxt = newGame.findViewById<TextView>(R.id.hasErrorTxt)
        val gameNameInput = newGame.findViewById<EditText>(R.id.newGameName)
        val gamePassword = newGame.findViewById<EditText>(R.id.gamePassword)
        val joinBtn = newGame.findViewById<Button>(R.id.joinBtn)
        val createBtn = newGame.findViewById<Button>(R.id.createBtn)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(newGame)
            .setMessage(getString(R.string.createOrJoin))
            .setNegativeButton(getString(R.string.cancel), /* listener = */ null)
            .create()

        viewModel.hasJoined.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                binding.root.findNavController().navigate(GamesDirections.actionGamesToHome2())
                dialog.cancel()
            }
            if (result == false) {
                hasErrorTxt.text = getString(R.string.wrongData)
                hasErrorTxt.visibility = View.VISIBLE
            }
        }

        viewModel.hasCreated.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                binding.root.findNavController().navigate(GamesDirections.actionGamesToHome2())
                dialog.cancel()
            }
            if (result == false) {
                hasErrorTxt.text = getString(R.string.nameTaken)
                hasErrorTxt.visibility = View.VISIBLE
            }
        }

        joinBtn.setOnClickListener {
            val name = gameNameInput.text.toString()
            val password = gamePassword.text.toString()
            scope.launch {
                viewModel.joinGame(name, password)
            }
        }

        createBtn.setOnClickListener {
            val name = gameNameInput.text.toString()
            val password = gamePassword.text.toString()
            scope.launch {
                viewModel.createGame(name, password)
            }
        }

        return dialog
    }
}
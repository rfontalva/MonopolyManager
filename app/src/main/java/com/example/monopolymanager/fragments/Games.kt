package com.example.monopolymanager.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolymanager.R
import com.example.monopolymanager.adapters.GameAdapter
import com.example.monopolymanager.adapters.PropertyAdapter
import com.example.monopolymanager.databinding.FragmentGamesBinding
import com.example.monopolymanager.viewmodels.GamesViewModel

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

        binding.gamesList.setHasFixedSize(true)
        binding.gamesList.layoutManager = LinearLayoutManager(context)
        binding.gamesList.adapter = GameAdapter(games = viewModel.getGames("r")){ index->
            onItemClick(index)
        }
    }

    fun onItemClick(position: Int) {
        viewModel.setGame(position)
//        binding.root.findNavController().navigate()
    }
}
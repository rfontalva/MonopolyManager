package com.example.monopolymanager.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.monopolymanager.R
import com.example.monopolymanager.viewmodels.SellDetailViewModel

class SellDetail : Fragment() {

    companion object {
        fun newInstance() = SellDetail()
    }

    private lateinit var viewModel: SellDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sell_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SellDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
package com.example.casino.ui.roulettes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casino.data.business_objects.Roulette
import com.example.casino.data.contract.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoulettesViewModel(
    private val dataManager: DataManager
) : ViewModel() {

    val roulettes: MutableLiveData<List<Roulette>> = MutableLiveData(listOf())

    fun loadRoulettes() {
        viewModelScope.launch(Dispatchers.IO) {
            roulettes.postValue(dataManager.getRoulettes())
        }
    }

}
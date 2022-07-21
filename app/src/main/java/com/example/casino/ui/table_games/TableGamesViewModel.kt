package com.example.casino.ui.table_games

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casino.data.business_objects.TableGame
import com.example.casino.data.contract.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TableGamesViewModel(
    private val dataManager: DataManager
) : ViewModel() {

    val tableGames: MutableLiveData<List<TableGame>> = MutableLiveData(listOf())

    fun loadTableGames() {
        viewModelScope.launch(Dispatchers.IO) {
            tableGames.postValue(dataManager.getTableGames())
        }
    }

}
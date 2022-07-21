package com.example.casino.ui.slots

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casino.data.business_objects.Slot
import com.example.casino.data.contract.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SlotsViewModel(
    private val dataManager: DataManager
) : ViewModel() {

    val slots: MutableLiveData<List<Slot>> = MutableLiveData(listOf())

    fun loadSlots() {
        viewModelScope.launch(Dispatchers.IO) {
            slots.postValue(dataManager.getSlots())
        }
    }

}
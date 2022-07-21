package com.example.casino.ui.sport

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casino.R
import com.example.casino.data.business_objects.Match
import com.example.casino.data.business_objects.Sport
import com.example.casino.data.contract.DataManager
import com.example.casino.ui.views.Banner
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SportViewModel(
    private val dataManager: DataManager
) : ViewModel() {

    private val selectedSport: MutableLiveData<Sport> = MutableLiveData(Sport.SOCCER)
    private val allMatches: MutableLiveData<MutableMap<Sport, List<Match>>> =
        MutableLiveData(mutableMapOf())
    private val loadersInfo: MutableLiveData<MutableMap<Sport, Boolean>> =
        MutableLiveData(mutableMapOf())
    private val mutex = Mutex()
    val selectedSportMatches: MediatorLiveData<List<Match>?> =
        MediatorLiveData<List<Match>?>().apply {
            addSource(allMatches) {
                value = it[selectedSport.value!!]
            }
            addSource(selectedSport) {
                value = allMatches.value?.get(it)
            }
        }
    val selectedSportMatchesIsLoading: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSource(loadersInfo) {
                value = it[selectedSport.value!!] ?: false
            }
            addSource(selectedSport) {
                value = loadersInfo.value?.get(selectedSport.value) ?: false
            }
        }

    fun getBanners(): List<Banner> {
        return listOf(
            Banner(
                R.drawable.sport_banner_1,
                0
            ),
            Banner(
                R.drawable.sport_banner_2,
                1
            ),
            Banner(
                R.drawable.sport_banner_3,
                2
            )
        )
    }

    fun loadMatches(sport: Sport) {
        selectedSport.postValue(sport)
        viewModelScope.launch {
            mutex.withLock {
                val map = loadersInfo.value!!
                map[sport] = true
                loadersInfo.postValue(map)
            }
            val matchesResult = dataManager.getMatches(sport)
            if (matchesResult.isSuccess()) {
                val matches = matchesResult.data.orEmpty()
                mutex.withLock {
                    val map = allMatches.value!!
                    map[sport] = matches
                    allMatches.postValue(map)
                }
                mutex.withLock {
                    val map = loadersInfo.value!!
                    map[sport] = false
                    loadersInfo.postValue(map)
                }
            } else {
                Log.d("MainViewModel", "error:${matchesResult.status.error?.message}")
            }
        }
    }
}
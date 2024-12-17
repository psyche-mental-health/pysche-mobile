package com.example.psyche.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.psyche.data.HistoryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _latestHistory = MutableLiveData<List<HistoryData>>()
    val latestHistory: LiveData<List<HistoryData>> get() = _latestHistory

    private val _isFetchingData = MutableLiveData<Boolean>()
    val isFetchingData: LiveData<Boolean> get() = _isFetchingData

    private var lastFetchedTimestamp: Long = 0

    fun fetchUserName() {
        _isFetchingData.value = true
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _userName.value = document.getString("name")
                    }
                    _isFetchingData.value = false
                }
        } else {
            _isFetchingData.value = false
        }
    }

    fun fetchLatestHistory() {
        _isFetchingData.value = true
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .collection("mental_health_results")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    val latestHistory = documents.map { it.toObject(HistoryData::class.java) }
                    if (latestHistory.isNotEmpty()) {
                        _latestHistory.value = latestHistory
                        lastFetchedTimestamp = latestHistory[0].timestamp
                    }
                    _isFetchingData.value = false
                }
        } else {
            _isFetchingData.value = false
        }
    }

    fun checkForUpdates() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .collection("mental_health_results")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    val latestHistory = documents.map { it.toObject(HistoryData::class.java) }
                    if (latestHistory.isNotEmpty() && latestHistory[0].timestamp > lastFetchedTimestamp) {
                        fetchLatestHistory()
                    }
                }
        }
    }
}
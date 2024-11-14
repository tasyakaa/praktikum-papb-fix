package com.example.prakpapb_m2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prakpapb_m2.data.model.local.Tugas
import com.example.prakpapb_m2.data.model.local.TugasRepository
import kotlinx.coroutines.launch

class MainViewModel(private val tugasRepository: TugasRepository) : ViewModel() {
//    private val profileRepository = ProfileRepository()

    // LiveData for the list of tasks
    private val _tugasList = tugasRepository.getAllTugas()
    val tugasList: LiveData<List<Tugas>> get() = _tugasList

//    // Other LiveData properties
//    private val _user = MutableLiveData<Profile?>()
//    val user: LiveData<Profile?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAllTugas()
    }

    private fun fetchAllTugas() {
        viewModelScope.launch {
            tugasRepository.getAllTugas()
        }
    }

    fun addTugas(matkul: String, detailTugas: String) {
        val newTugas = Tugas(matkul = matkul, detailTugas = detailTugas, selesai = false)
        viewModelScope.launch {
            tugasRepository.insert(newTugas) // Call the insert function
        }
    }

//    fun getProfileUser(username: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val profile = profileRepository.getProfile(username)
//                _user.value = profile
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = e.message
//                _user.value = null
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
}

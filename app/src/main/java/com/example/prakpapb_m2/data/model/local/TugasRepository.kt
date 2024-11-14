package com.example.prakpapb_m2.data.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val ntugasDao: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = TugasDB.getDatabase(application)
        ntugasDao = db.tugasDao()
    }
    fun getAllTugas(): LiveData<List<Tugas>> = ntugasDao.getAllTugas()
    fun insert(tugas: Tugas) {
        executorService.execute { ntugasDao.insertTugas(tugas) }
    }
}

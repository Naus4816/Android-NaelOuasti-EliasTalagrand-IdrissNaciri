package com.example.myapplication

import android.app.Application

class MyApplication : Application() {
    var joursRestants: Int = 3
    var entrainementsRealises: Int = 0
    var tour: Int = 1

    fun updateJoursRestants(newJoursRestants: Int) {
        joursRestants = newJoursRestants
    }
    fun updateEntrainementsRealises(newEntrainementsRealises: Int) {
        entrainementsRealises = newEntrainementsRealises
    }
    fun updateTour(newTour: Int)
    {
        tour = newTour
    }
}

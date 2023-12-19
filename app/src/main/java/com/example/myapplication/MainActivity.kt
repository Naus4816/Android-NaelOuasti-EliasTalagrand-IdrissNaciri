package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        setContentView(R.layout.main_activity)

        // Création du canal de notification
        createNotificationChannel()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val competitionFragment = CompetitionFragment()
        val matchFragment = MatchFragment()
        val nouvellePartieFragment = NouvellePartieFragment()
        val votreEquipeFragment = VotreEquipeFragment()

        setCurrentFragment(matchFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.competition -> setCurrentFragment(competitionFragment)
                R.id.match -> setCurrentFragment(matchFragment)
                R.id.nouvelle_partie -> setCurrentFragment(nouvellePartieFragment)
                R.id.votre_equipe -> setCurrentFragment(votreEquipeFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.mainActivity, fragment).commit()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "champions_channel"
            val channelName = "Champions Channel"
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

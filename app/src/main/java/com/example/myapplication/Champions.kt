package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment

class ChampionsFragment : Fragment() {

    private val channelId = "champions_channel"
    private val notificationId = 1 // L'ID de la notification doit être unique pour chaque notification

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_champions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Placer le code de notification ici
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle("Félicitations!")
            .setContentText("Vous avez gagné!!!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(requireContext())

        // Demander la permission POST_NOTIFICATIONS si elle n'est pas déjà accordée
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si la permission n'est pas accordée, demandez-la
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        } else {
            // Si la permission est déjà accordée, affichez la notification
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}

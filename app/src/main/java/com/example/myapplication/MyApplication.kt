package com.example.myapplication

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {
    var Data: Data = Data()

    override fun onCreate() {
        super.onCreate()

        // Appeler la fonction pour récupérer les données au démarrage de l'application
        fetchDataFromFirestore()
    }

    fun updateData(newJoursRestants: Int, newEntrainementsRealises: Int, newTour: Int) {
        Data = Data(newJoursRestants, newEntrainementsRealises, newTour)
    }

    // Fonction pour récupérer les données depuis la collection "data" de Firestore
    private fun fetchDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val dataDocument = db.collection("data").document("your_document_id") // Remplacez par votre document ID

        dataDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Récupérer les données et mettre à jour Data
                    val joursRestants = documentSnapshot.getLong("joursRestants")?.toInt() ?: 3
                    val entrainementsRealises = documentSnapshot.getLong("entrainementsRealises")?.toInt() ?: 0
                    val tour = documentSnapshot.getLong("tour")?.toInt() ?: 1

                    updateData(joursRestants, entrainementsRealises, tour)
                } else {
                    // Document non trouvé, utiliser les valeurs par défaut
                    updateData(3, 0, 1)
                }
            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs ici
                updateData(3, 0, 1)
            }
    }
}

class Data(
    var joursRestants: Int = 3,
    var entrainementsRealises: Int = 0,
    var tour: Int = 1
) {
    fun updateJoursRestants(newJoursRestants: Int) {
        joursRestants = newJoursRestants
        updateFirestore()
    }

    fun updateEntrainementsRealises(newEntrainementsRealises: Int) {
        entrainementsRealises = newEntrainementsRealises
        updateFirestore()
    }

    fun updateTour(newTour: Int) {
        tour = newTour
        updateFirestore()
    }

    private fun updateFirestore() {
        val db = FirebaseFirestore.getInstance()
        val dataDocument = db.collection("data").document("0lz3FSdeQpaDfzDtLUY1")

        val updater= hashMapOf(
            "joursRestants" to joursRestants,
            "entrainementsRealises" to entrainementsRealises,
            "tour" to tour
        )

        dataDocument
            .update(updater as Map<String, Any>)
            .addOnSuccessListener {
                // Gérer le succès de la mise à jour
            }
            .addOnFailureListener { exception ->
                // Gérer les erreurs ici
            }
    }
}

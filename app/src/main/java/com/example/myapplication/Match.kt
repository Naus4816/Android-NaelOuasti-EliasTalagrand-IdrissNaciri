package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlin.random.Random

class Match(
    val id: String,
    val equipe1: String,
    val equipe2: String,
    var score1: Int,
    var score2: Int,
    var tour_match: Int
)

class MatchFragment : Fragment(R.layout.fragment_match) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val equipe1TextView: TextView = view.findViewById(R.id.Equipe1)
        val equipe2TextView: TextView = view.findViewById(R.id.Equipe2)
        val scoreTextView: TextView = view.findViewById(R.id.Score)
        val btnMatch: Button = view.findViewById(R.id.btnMatch) ?: return

        val myApp = requireActivity().application as MyApplication
        var joursRestants = myApp.Data.joursRestants
        var tour = myApp.Data.tour

        val equipeChoisieCollection =
            FirebaseFirestore.getInstance().collection("equipe_choisie")

        equipeChoisieCollection.get().addOnSuccessListener { equipeSnapshot ->
            if (!equipeSnapshot.isEmpty) {
                val equipeDocument = equipeSnapshot.documents[0]
                val equipeChoisie = Equipe(
                    equipeDocument.getString("nom") ?: "",
                    equipeDocument.id
                )
                Log.d("equipechoisie","equipechoisie:${equipeChoisie.id}")

                val matchsCollection =
                    FirebaseFirestore.getInstance().collection("match")

                val matchQueryEquipe1 =
                    matchsCollection.whereEqualTo("equipe1", equipeChoisie.nom)
                val matchQueryEquipe2 =
                    matchsCollection.whereEqualTo("equipe2", equipeChoisie.nom)

                val combinedQuery: Task<List<QuerySnapshot>> =
                    Tasks.whenAllSuccess(matchQueryEquipe1.get(), matchQueryEquipe2.get())

                var match: Match? = null

                combinedQuery.addOnSuccessListener { taskSnapshots ->
                    val matchSnapshotEquipe1 = taskSnapshots[0] as QuerySnapshot
                    val matchSnapshotEquipe2 = taskSnapshots[1] as QuerySnapshot

                    if (!matchSnapshotEquipe1.isEmpty || !matchSnapshotEquipe2.isEmpty) {
                        // Au moins une des deux requêtes a retourné des résultats
                        // Utilisez les résultats comme nécessaire
                        if (!matchSnapshotEquipe1.isEmpty) {
                            // L'équipe est dans la position equipe1
                            val matchDocument = matchSnapshotEquipe1.documents[0]
                             match = Match(
                                matchDocument.id,
                                matchDocument.getString("equipe1") ?: "",
                                matchDocument.getString("equipe2") ?: "",
                                matchDocument.getLong("score1")?.toInt() ?: 0,
                                matchDocument.getLong("score2")?.toInt() ?: 0,
                                matchDocument.getLong("tour")?.toInt() ?: 0
                            )

                            // Faites quelque chose avec l'objet Match...

                        } else {
                            // L'équipe est dans la position equipe2
                            val matchDocument = matchSnapshotEquipe2.documents[0]
                             match = Match(
                                matchDocument.id,
                                matchDocument.getString("equipe1") ?: "",
                                matchDocument.getString("equipe2") ?: "",
                                matchDocument.getLong("score1")?.toInt() ?: 0,
                                matchDocument.getLong("score2")?.toInt() ?: 0,
                                matchDocument.getLong("tour")?.toInt() ?: 0
                            )

                            // Faites quelque chose avec l'objet Match...

                        }
                        equipe1TextView.text = match!!.equipe1
                        equipe2TextView.text = match!!.equipe2
                        btnMatch.isEnabled = joursRestants == 0
                        btnMatch.visibility = View.VISIBLE

                        btnMatch.setOnClickListener {
                            var score1: Int
                            var score2: Int
                            val nom = match!!.equipe1
                            val db = FirebaseFirestore.getInstance()
                            val equipeCollection = db.collection("equipe")
                            // Récupérer les notes des équipes à partir de la collection "equipe"
                            val equipe1NoteQuery = equipeCollection.whereEqualTo("nom", nom)
                            Log.d("Snapshot","Snapshot:$nom")
                            equipe1NoteQuery.get().addOnSuccessListener { equipe1Snapshot ->
                                if (!equipe1Snapshot.isEmpty) {
                                    Log.d("note","note")
                                    val noteEquipe1 = equipe1Snapshot.documents[0].getLong("note")?.toInt() ?: 0

                                    val equipe2NoteQuery = equipeCollection.whereEqualTo("nom", nom)
                                    equipe2NoteQuery.get().addOnSuccessListener { equipe2Snapshot ->
                                        if (!equipe2Snapshot.isEmpty) {
                                            val noteEquipe2 = equipe2Snapshot.documents[0].getLong("note")?.toInt() ?: 0

                                            // Utiliser la note de l'équipe2
                                            // ...

                                            if (noteEquipe1 > noteEquipe2) {
                                                score1 = Random.nextInt(1, 11)
                                                score2 = Random.nextInt(1, score1)
                                            } else {
                                                score2 = Random.nextInt(1, 11)
                                                score1 = Random.nextInt(1, score2)
                                            }

                                            while (score1 == score2 || score1 >= 10 || score2 >= 10) {
                                                score1 = Random.nextInt(1, 11)
                                                score2 = Random.nextInt(1, 11)
                                            }

                                            if (score1 != 0 || score2 != 0) {
                                                scoreTextView.text = "        $score1:$score2"
                                            } else {
                                                scoreTextView.visibility = View.GONE
                                            }

                                            if (score1 > score2) {
                                                scoreTextView.append("\nWIN")
                                                tour++
                                                myApp.Data.updateTour(tour)

                                                if (tour == 3) {
                                                    tour = 0
                                                    myApp.Data.updateTour(tour)
                                                    val championsFragment = ChampionsFragment()
                                                    val transaction =
                                                        requireActivity().supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        R.id.mainActivity,
                                                        championsFragment
                                                    )
                                                    transaction.addToBackStack(null)
                                                    transaction.commit()
                                                }
                                            } else {
                                                val looseFragment = LooseFragment()
                                                val transaction =
                                                    requireActivity().supportFragmentManager.beginTransaction()
                                                transaction.replace(
                                                    R.id.mainActivity,
                                                    looseFragment
                                                )
                                                transaction.addToBackStack(null)
                                                transaction.commit()
                                            }

                                            myApp.Data.updateJoursRestants(3)
                                            joursRestants = myApp.Data.joursRestants
                                            tour = myApp.Data.tour
                                            btnMatch.isEnabled = joursRestants == 0
                                        }
                                    }
                                } else {
                                    Log.d("Firestore", "Aucun match trouvé pour l'équipe choisie")
                                }
                            }
                        }
                    }
                    else {
                            Log.d("Firestore", "Aucune équipe choisie trouvée dans la collection equipe_choisie")
                            }
                        }
                    }
                }
            }
        }

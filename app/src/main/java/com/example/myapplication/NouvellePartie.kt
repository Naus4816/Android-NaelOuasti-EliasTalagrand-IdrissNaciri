package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class NouvellePartieFragment : Fragment() {
    private var equipeChoisie: Equipe? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nouvelle_partie, container, false)

        val db = FirebaseFirestore.getInstance()
        val matchsCollection = db.collection("match")
        val myApp = requireActivity().application as MyApplication
        var tour = myApp.Data.tour

        matchsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }


            if (snapshot != null && !snapshot.isEmpty) {
                val matchs = mutableListOf<Match>()

                for (document in snapshot) {
                    val id = document.id
                    Log.d("id", "id: $id")
                    val equipe1 = document.getString("equipe1") ?: ""
                    Log.d("id", "id: $equipe1")
                    val equipe2 = document.getString("equipe2") ?: ""
                    Log.d("id", "id: $equipe2")
                    val score1 = document.getLong("score1")?.toInt() ?: 0
                    Log.d("id", "id: $score1")
                    val score2 = document.getLong("score2")?.toInt() ?: 0
                    Log.d("id", "id: $score2")

                    val match = Match(id, equipe1, equipe2, score1, score2,tour)
                    matchs.add(match)
                }

                displayMatchesInTable(view, matchs)
            } else {
                Log.d("Firestore", "Current data: null")
            }
        }

        return view
    }

    private fun displayMatchesInTable(view: View, matchs: List<Match>) {
        for (i in matchs.indices) {
            val tableau: TableLayout = when (i + 1) {
                1 -> view.findViewById(R.id.match1)
                2 -> view.findViewById(R.id.match2)
                3 -> view.findViewById(R.id.match3)
                4 -> view.findViewById(R.id.match4)
                else -> throw IllegalArgumentException("Erreur d'indice")
            }

            val match = matchs[i]

            val newRow1 = TableRow(requireContext())
            val equipe1TextView = TextView(requireContext())
            val score1TextView = TextView(requireContext())

            equipe1TextView.text = match.equipe1
            equipe1TextView.gravity = Gravity.CENTER
            score1TextView.text = match.score1.toString()
            score1TextView.gravity = Gravity.CENTER

            newRow1.addView(
                equipe1TextView,
                TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            )
            newRow1.addView(
                score1TextView,
                TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            )

            // Ajout de la ligne au tableau
            tableau.addView(newRow1)

            val newRow2 = TableRow(requireContext())
            val equipe2TextView = TextView(requireContext())
            val score2TextView = TextView(requireContext())

            // Définissez les données de l'équipe 2
            equipe2TextView.text = match.equipe2
            equipe2TextView.gravity = Gravity.CENTER
            score2TextView.text = match.score2.toString()
            score2TextView.gravity = Gravity.CENTER

            newRow2.addView(
                equipe2TextView,
                TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            )
            newRow2.addView(
                score2TextView,
                TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            )

            // Ajoutez la ligne au tableau
            tableau.addView(newRow2)

            //TODO:Ajouter l'equipe choisi à la bdd
            equipe1TextView.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val equipeCollection = db.collection("equipe")
                val equipeChoisieCollection = db.collection("equipe_choisie")
                val stats_en_cours = db.collection("stats_en_cours")
                val matchs = db.collection("match")
                val equipeQuery = equipeCollection.whereEqualTo("nom", match.equipe1)

                equipeQuery.addSnapshotListener { equipeSnapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (equipeSnapshot != null && !equipeSnapshot.isEmpty) {
                        val equipeDocument = equipeSnapshot.documents[0]
                        val idEquipe1 = equipeDocument.id
                        Log.d("Firestore", "ID de l'équipe1: $idEquipe1")

                        // Suppression des équipes existantes dans "equipe_choisie"
                        equipeChoisieCollection.get().addOnSuccessListener { equipeSnapshotToDelete ->
                            val batch = db.batch()
                            for (equipeDocument in equipeSnapshotToDelete.documents) {
                                val equipeDocRef = equipeChoisieCollection.document(equipeDocument.id)
                                batch.delete(equipeDocRef)
                            }

                            // Suppression des stats dans "stats_en_cours"
                            val statsQuery = stats_en_cours

                            statsQuery.get().addOnSuccessListener { statsSnapshot ->
                                val batch = FirebaseFirestore.getInstance().batch()

                                for (statsDocument in statsSnapshot.documents) {
                                    // Ajouter les suppressions à la transaction batch
                                    val statsDocRef = stats_en_cours.document(statsDocument.id)
                                    batch.delete(statsDocRef)
                                }

                                // Exécuter la transaction batch
                                batch.commit().addOnSuccessListener {
                                    // Succès de la suppression
                                    // Faire quelque chose après la suppression réussie, si nécessaire
                                }.addOnFailureListener { e ->
                                    // Gérer les erreurs ici
                                    // e.printStackTrace() pour imprimer la trace d'erreur, si nécessaire
                                }
                            }.addOnFailureListener { e ->
                                // Gérer les erreurs ici
                                // e.printStackTrace() pour imprimer la trace d'erreur, si nécessaire
                            }

                                // Suppression des matchs qui n'ont pas l'un des ID spécifiés
                                val matchsQuery = matchs.whereNotIn(
                                    "id",
                                    listOf(
                                        "7QAQpxmEDhqi6PhYLfkR",
                                        "Yey6cf4l6b8LmD6pJcPw",
                                        "cUSDssAsyBSQ5JGwKnRi",
                                        "chNne2v4X8LclsCtpTPp"
                                    )
                                )
                                matchsQuery.get().addOnSuccessListener { matchsSnapshot ->
                                    for (matchDocument in matchsSnapshot.documents) {
                                        val matchDocRef = matchs.document(matchDocument.id)
                                        batch.delete(matchDocRef)
                                    }

                                    // Exécuter le batch de suppressions
                                    batch.commit()
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Toutes les équipes existantes ont été supprimées avec succès")

                                            // Ajout de la nouvelle équipe à "equipe_choisie"
                                            val nouvelleEquipe = hashMapOf(
                                                "id_equipe" to idEquipe1,
                                                "nom" to match.equipe1
                                            )

                                            db.runTransaction { transaction ->
                                                transaction.set(equipeChoisieCollection.document(), nouvelleEquipe)
                                                null
                                            }.addOnSuccessListener {
                                                Log.d("Firestore", "Transaction réussie")
                                                val matchFragment = MatchFragment()
                                                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                                transaction.replace(R.id.mainActivity, matchFragment)
                                                transaction.addToBackStack(null)
                                                transaction.commit()
                                            }.addOnFailureListener { exception ->
                                                Log.w("Firestore", "Transaction échouée", exception)
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("Firestore", "Erreur lors de la suppression des équipes existantes", exception)
                                        }
                                }
                            }
                        } else {
                        Log.d("Firestore", "Aucune équipe trouvée avec le nom ${match.equipe2}")
                    }
                        }
                    }

                    equipe2TextView.setOnClickListener {
                        val db = FirebaseFirestore.getInstance()
                        val equipeCollection = db.collection("equipe")
                        val equipeChoisieCollection = db.collection("equipe_choisie")
                        val stats_en_cours = db.collection("stats_en_cours")
                        val matchs = db.collection("match")
                        val equipeQuery = equipeCollection.whereEqualTo("nom", match.equipe2)

                        equipeQuery.addSnapshotListener { equipeSnapshot, e ->
                            if (e != null) {
                                Log.w("Firestore", "Listen failed.", e)
                                return@addSnapshotListener
                            }

                            if (equipeSnapshot != null && !equipeSnapshot.isEmpty) {
                                val equipeDocument = equipeSnapshot.documents[0]
                                val idEquipe2 = equipeDocument.id
                                Log.d("Firestore", "ID de l'équipe2: $idEquipe2")

                                // Suppression des équipes existantes dans "equipe_choisie"
                                equipeChoisieCollection.get()
                                    .addOnSuccessListener { equipeSnapshotToDelete ->
                                        val batch = db.batch()
                                        for (equipeDocumentToDelete in equipeSnapshotToDelete.documents) {
                                            val equipeDocRef = equipeChoisieCollection.document(
                                                equipeDocumentToDelete.id
                                            )
                                            batch.delete(equipeDocRef)
                                        }
                                        // Suppression des stats dans "stats_en_cours"
                                        val statsQuery = stats_en_cours

                                        statsQuery.get().addOnSuccessListener { statsSnapshot ->
                                            val batch = FirebaseFirestore.getInstance().batch()

                                            for (statsDocument in statsSnapshot.documents) {
                                                // Ajouter les suppressions à la transaction batch
                                                val statsDocRef =
                                                    stats_en_cours.document(statsDocument.id)
                                                batch.delete(statsDocRef)
                                            }

                                            // Exécuter la transaction batch
                                            batch.commit().addOnSuccessListener {
                                                // Succès de la suppression
                                                // Faire quelque chose après la suppression réussie, si nécessaire
                                            }.addOnFailureListener { e ->
                                                // Gérer les erreurs ici
                                                // e.printStackTrace() pour imprimer la trace d'erreur, si nécessaire
                                            }
                                        }.addOnFailureListener { e ->
                                            // Gérer les erreurs ici
                                            // e.printStackTrace() pour imprimer la trace d'erreur, si nécessaire
                                        }

                                        // Suppression des matchs qui n'ont pas l'un des ID spécifiés
                                        val matchsQuery = matchs.whereNotIn(
                                            "id",
                                            listOf(
                                                "7QAQpxmEDhqi6PhYLfkR",
                                                "Yey6cf4l6b8LmD6pJcPw",
                                                "cUSDssAsyBSQ5JGwKnRi",
                                                "chNne2v4X8LclsCtpTPp"
                                            )
                                        )

                                        matchsQuery.get().addOnSuccessListener { matchsSnapshot ->
                                            for (matchDocument in matchsSnapshot.documents) {
                                                val matchDocRef = matchs.document(matchDocument.id)
                                                batch.delete(matchDocRef)
                                            }

                                            // Exécuter le batch de suppressions
                                            batch.commit()
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        "Firestore",
                                                        "Toutes les équipes existantes ont été supprimées avec succès"
                                                    )

                                                    // Ajout de la nouvelle équipe à "equipe_choisie"
                                                    val nouvelleEquipe = hashMapOf(
                                                        "id_equipe" to idEquipe2,
                                                        "nom" to match.equipe2
                                                    )

                                                    db.runTransaction { transaction ->
                                                        transaction.set(
                                                            equipeChoisieCollection.document(),
                                                            nouvelleEquipe
                                                        )
                                                        null
                                                    }.addOnSuccessListener {
                                                        Log.d("Firestore", "Transaction réussie")
                                                        val matchFragment = MatchFragment()
                                                        val transaction =
                                                            requireActivity().supportFragmentManager.beginTransaction()
                                                        transaction.replace(
                                                            R.id.mainActivity,
                                                            matchFragment
                                                        )
                                                        transaction.addToBackStack(null)
                                                        transaction.commit()
                                                    }.addOnFailureListener { exception ->
                                                        Log.w(
                                                            "Firestore",
                                                            "Transaction échouée",
                                                            exception
                                                        )
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.w(
                                                        "Firestore",
                                                        "Erreur lors de la suppression des équipes existantes",
                                                        exception
                                                    )
                                                }
                                        }
                                    }
                            } else {
                                Log.d(
                                    "Firestore",
                                    "Aucune équipe trouvée avec le nom ${match.equipe2}"
                                )
                            }
                        }
                    }
                }
            }
        }

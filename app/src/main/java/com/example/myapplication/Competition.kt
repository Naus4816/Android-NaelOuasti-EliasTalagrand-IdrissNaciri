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

class CompetitionFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_competition, container, false)
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
                    Log.d("id", "id: $score1")

                    val match = Match(id, equipe1, equipe2, score1, score2, tour)
                    matchs.add(match)
                }

                // Utilisation des matchs comme votre liste de matchs chargés depuis Firestore
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
                5 -> view.findViewById(R.id.match5)
                6 -> view.findViewById(R.id.match6)
                7 -> view.findViewById(R.id.match7)
                else -> throw IllegalArgumentException("Erreur d'indice")
            }

            val match = matchs[i]

            addTableRowToTable(tableau, match.equipe1, match.score1)
            addTableRowToTable(tableau, match.equipe2, match.score2)
        }
    }

    private fun addTableRowToTable(table: TableLayout, equipe: String, score: Int) {
        val newRow = TableRow(requireContext())
        val equipeTextView = TextView(requireContext())
        val scoreTextView = TextView(requireContext())

        equipeTextView.text = equipe
        equipeTextView.gravity = Gravity.CENTER
        scoreTextView.text = score.toString()
        scoreTextView.gravity = Gravity.CENTER

        // Ajout d'un identifiant unique à chaque joueur
        equipeTextView.setTag(R.id.joueur_id, equipe)
        scoreTextView.setTag(R.id.joueur_id, equipe)

        newRow.addView(
            equipeTextView,
            TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        )
        newRow.addView(
            scoreTextView,
            TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        )

        // Ajout de la ligne au tableau
        table.addView(newRow)
    }
}

package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

class Match(val equipe1: String, val equipe2: String, var score1: Int, var score2: Int, val id: Int)

class MatchFragment : Fragment(R.layout.fragment_match) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val equipe1TextView: TextView = view.findViewById(R.id.Equipe1)
        val equipe2TextView: TextView = view.findViewById(R.id.Equipe2)
        val scoreTextView: TextView = view.findViewById(R.id.Score)

        // TODO: Charger les valeurs des notes des équipes depuis la base de données
        val noteEquipe1 = 89
        val noteEquipe2 = 90

        val match = Match("Equipe 1", "Equipe 2", 0, 0, 1)

        equipe1TextView.text = match.equipe1
        equipe2TextView.text = match.equipe2


        val myApp = requireActivity().application as MyApplication
        var joursRestants = myApp.joursRestants
        var tour = myApp.tour



        val btnMatch: Button = view.findViewById(R.id.btnMatch) ?: return

            btnMatch.isEnabled = joursRestants == 0

            btnMatch.visibility = View.VISIBLE
            btnMatch.setOnClickListener {
                // Comparer les notes et attribuer les scores en conséquence
                var score1: Int
                var score2: Int

                if (noteEquipe1 > noteEquipe2) {
                    score1 = Random.nextInt(1, 11)
                    score2 = Random.nextInt(1, score1)
                } else {
                    score2 = Random.nextInt(1, 11)
                    score1 = Random.nextInt(1, score2)
                }

                // S'assurer que les scores ne sont pas égaux, entiers positifs et inférieurs à 10
                while (score1 == score2 || score1 >= 10 || score2 >= 10) {
                    score1 = Random.nextInt(1, 11)
                    score2 = Random.nextInt(1, 11)
                }

                if (score1 != 0 || score2 != 0) {
                    scoreTextView.text = "        $score1:$score2"
                }
                else {
                    scoreTextView.visibility = View.GONE
                }
                if (score1 > score2) {
                    scoreTextView.append("\nWIN")
                    tour ++
                    myApp.updateTour(tour)
                    Log.d("tour", "tour $tour")

                    // Si c'est la finale, démarrer le fragment des champions
                    if (tour == 3) {
                        tour = 0
                        myApp.updateTour(tour)
                        val championsFragment = ChampionsFragment()

                        // Remplacez le fragment actuel par le fragment des champions
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.mainActivity, championsFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                } else {
                    // Si l'équipe 1 perd, démarrer le fragment de défaite
                    val looseFragment = LooseFragment()

                    // Remplacez le fragment actuel par le fragment de défaite
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainActivity, looseFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                myApp.updateJoursRestants(3)
                joursRestants = myApp.joursRestants
                tour = myApp.tour
                btnMatch.isEnabled = joursRestants == 0
            }
    }

}

package com.example.myapplication

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class NouvellePartieFragment : Fragment(){
    // Notez que findViewById doit être appelé à l'intérieur de onViewCreated

    private var equipeChoisie: Equipe? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialiser tableLayout dans onViewCreated
        val view = inflater.inflate(R.layout.fragment_nouvelle_partie, container, false)

        // C'est ici que devrait se faire le chargement des matchs depuis la bdd
        val matchs = listOf(
            Match("Équipe 1", "Équipe 2", 2, 1, 1),
            Match("Équipe 3", "Équipe 4", 0, 1, 2),
            Match("Équipe 5", "Équipe 6", 3, 0, 3),
            Match("Équipe 7", "Équipe 8", 1, 4, 4),
            // ... ajoutez autant d'équipes que nécessaire
        )


        // Ajouter chaque équipe au tableau
        for (i in matchs.indices) {
            val tableau: TableLayout = when (i+1) {
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

            // Définissez les données de l'équipe 1
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

            // Ajoutez la ligne au tableau
            tableau.addView(newRow1)

            val newRow2 = TableRow(requireContext())
            val equipe2TextView = TextView(requireContext())
            val score2TextView = TextView(requireContext())

            // Définissez les données de l'équipe 2
            equipe2TextView.text = match.equipe2
            equipe2TextView.gravity = Gravity.CENTER
            score2TextView.text = match.score2.toString()
            score2TextView.gravity = Gravity.CENTER

            // Ajoutez un identifiant unique à chaque joueur
            equipe2TextView.setTag(R.id.joueur_id, match.id)
            score2TextView.setTag(R.id.joueur_id, match.id)

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
                equipeChoisie = Equipe(match.equipe1, match.id)
                val matchFragment = MatchFragment()

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainActivity, matchFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            equipe2TextView.setOnClickListener {
                equipeChoisie = Equipe(match.equipe2, match.id)
                val matchFragment = MatchFragment()

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainActivity, matchFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        return view
    }
}
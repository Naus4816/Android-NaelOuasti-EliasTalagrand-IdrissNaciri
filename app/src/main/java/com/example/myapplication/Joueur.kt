package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

class Joueur(
    val nom: String,
    val note: String,
    val type: String,
    val id_stat: Int,
    val id_stat_goal: Int,
    val id: Int
)

class JoueurFragment : Fragment() {

    // Exemple de stats et stats_goal, vous devrez ajuster cela en fonction de vos besoins
    private lateinit var stats: Stats
    private lateinit var stats_goal: Stats_goal
    private lateinit var joueur: Joueur


    companion object {
        fun newInstance(joueurId: Int): JoueurFragment {
            val fragment = JoueurFragment()
            val args = Bundle()
            args.putInt("id", joueurId)
            fragment.arguments = args
            return fragment
        }
    }
    //TODO:La on à l'id du joueur qui permet de récupérer le joueur dans la bdd l'id est récupéré
    // avec cette fonction après normalement arguments?.getInt("id") ?: 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ... (autres initialisations)
        val view = inflater.inflate(R.layout.fragment_joueur, container, false)
        // Obtenez l'objet Joueur depuis les arguments
        joueur = Joueur(
            "Joueur1",
            "88",
            "Défenseur",
            1,
            0,
            arguments?.getInt("id") ?: 0
        )
        // Vérifiez si l'objet Joueur est non nul et si id_stat_goal est égal à 0
        if (joueur != null && joueur.id_stat_goal == 0) {
            stats = Stats(1, 2, 2, 2, 2, 2, 1)
            stats_goal = Stats_goal(0, 0, 0, 0, 0, 0, 0)
        } else {
            stats = Stats(0, 0, 0, 0, 0, 0, 0)
            stats_goal = Stats_goal(1, 2, 2, 2, 2, 2, 1)
        }

        val myApp = requireActivity().application as MyApplication
        var joursRestants = myApp.joursRestants
        var entrainementsRealises = myApp.entrainementsRealises

        // Récupérez le TableLayout
        val tableLayout: TableLayout = view?.findViewById(R.id.tableLayout) ?: return view

        // Ajoutez dynamiquement les informations du joueur au tableau
        addRowToTable("Nom", joueur.nom, tableLayout)
        addRowToTable("Note", joueur.note, tableLayout)
        addRowToTable("Type", joueur.type, tableLayout)
        if(joueur.id_stat_goal == 0) {
            addRowToTable("Dribble", stats.dribble.toString(), tableLayout)
            addRowToTable("Défense", stats.defense.toString(), tableLayout)
            addRowToTable("Passes", stats.passes.toString(), tableLayout)
            addRowToTable("Tir", stats.tir.toString(), tableLayout)
            addRowToTable("Vitesse", stats.vitesse.toString(), tableLayout)
        }
        else {
            addRowToTable("Degagement", stats_goal.degagement.toString(), tableLayout)
            addRowToTable("Jeu_de_main", stats_goal.jeu_de_main.toString(), tableLayout)
            addRowToTable("Plongeon", stats_goal.plongeon.toString(), tableLayout)
            addRowToTable("Jeu de main", stats_goal.jeu_de_main.toString(), tableLayout)
            addRowToTable("Vitesse", stats_goal.vitesse.toString(), tableLayout)
        }
        val textViewEntrainementsRealises: TextView = view.findViewById(R.id.textViewEntrainementsRealises)
        val textViewJoursRestants: TextView = view.findViewById(R.id.textViewJoursRestants)

        textViewEntrainementsRealises.text = "Entrainements réalisés: $entrainementsRealises"
        textViewJoursRestants.text = "Jours restants: $joursRestants"

        // Récupérez le bouton "Entraînement"
        if(joursRestants != 0)
        {
            val btnEntrainement: Button = view?.findViewById(R.id.btnEntrainement) ?: return view


            // Gérez le clic sur le bouton "Entraînement"
            btnEntrainement.setOnClickListener {

                entrainementsRealises += 1
                updateEntrainementsRealises(entrainementsRealises)

                tableLayout.removeViewAt(3) // Supprimez la ligne "Dribble"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Défense"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Passes"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Tir"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Vitesse"
                // Mettez à jour les statistiques de manière aléatoire
                if(joueur.id_stat_goal==0)
                {
                    stats.updateStatsRandomly()
                    addRowToTable("Dribble", stats.dribble.toString(), tableLayout)
                    addRowToTable("Défense", stats.defense.toString(), tableLayout)
                    addRowToTable("Passes", stats.passes.toString(), tableLayout)
                    addRowToTable("Tir", stats.tir.toString(), tableLayout)
                    addRowToTable("Vitesse", stats.vitesse.toString(), tableLayout)
                }
                else
                {
                    stats_goal.updateStatsGoalRandomly()
                    addRowToTable("Dribble", stats.dribble.toString(), tableLayout)
                    addRowToTable("Défense", stats.defense.toString(), tableLayout)
                    addRowToTable("Passes", stats.passes.toString(), tableLayout)
                    addRowToTable("Tir", stats.tir.toString(), tableLayout)
                    addRowToTable("Vitesse", stats.vitesse.toString(), tableLayout)
                }
                if (entrainementsRealises > 2)
                {
                    entrainementsRealises = 0
                    joursRestants--
                    updateJoursRestants(joursRestants)
                }
                textViewEntrainementsRealises.text = "Entrainements réalisés: $entrainementsRealises"
                textViewJoursRestants.text = "Jours restants: $joursRestants"


                // Désactiver le bouton s'il n'y a plus de jours restants
                if (joursRestants <= 0) {
                    updateEntrainementsRealises(entrainementsRealises)
                    btnEntrainement.isEnabled = false
                }

            }
        }
        return view
    }

    private fun addRowToTable(label: String, value: String, tableLayout: TableLayout) {
        val row = TableRow(requireContext())

        val labelTextView = TextView(requireContext())
        labelTextView.text = label
        labelTextView.gravity = Gravity.CENTER

        val valueTextView = TextView(requireContext())
        valueTextView.text = value
        valueTextView.gravity = Gravity.CENTER

        row.addView(labelTextView, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(valueTextView, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))

        tableLayout.addView(row)
    }
    fun Stats.updateStatsRandomly() {
        // Mettez à jour les statistiques de manière aléatoire
        dribble += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        defense += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        passes += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        tir += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        vitesse += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        // Mettez à jour d'autres statistiques selon vos besoins
    }

    fun Stats_goal.updateStatsGoalRandomly() {
        // Mettez à jour les statistiques de manière aléatoire
        degagement += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        jeu_de_main += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        plongeon += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        reflexes += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        vitesse += if (Random.nextBoolean()) Random.nextInt(1, 11) else 0
        // Mettez à jour d'autres statistiques selon vos besoins
    }

    private fun updateJoursRestants(joursRestants: Int) {
        val myApp = requireActivity().application as MyApplication
        myApp.updateJoursRestants(joursRestants)
    }
    private fun updateEntrainementsRealises(entrainementsRealises: Int) {
        val myApp = requireActivity().application as MyApplication
        myApp.updateEntrainementsRealises(entrainementsRealises)
    }

}

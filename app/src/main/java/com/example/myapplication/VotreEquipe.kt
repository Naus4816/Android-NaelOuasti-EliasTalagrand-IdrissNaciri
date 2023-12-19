package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment

class VotreEquipeFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_votre_equipe, container, false)

        // Ajouter vos joueurs ici (simulé pour cet exemple)
        val joueurs = listOf(
            Joueur("Joueur 1", "88", "Défenseur",1,0,1),
            Joueur("Joueur 2", "90", "Goal",0,1,1),
            // Ajoutez d'autres joueurs selon vos besoins
        )

        // Récupérez le conteneur pour le contenu du tableau
        val tableau: TableLayout = view.findViewById(R.id.tableLayout)

        // Récupérez le drawable pour la bordure des joueurs
        val playerBorder = resources.getDrawable(R.drawable.table_background)

        // Ajoutez dynamiquement les joueurs au tableau
        for (joueur in joueurs) {
            val row = TableRow(requireContext())
            val joueurTextView = TextView(requireContext())
            val noteTextView = TextView(requireContext())

            joueurTextView.text = joueur.nom
            joueurTextView.gravity = Gravity.CENTER

            noteTextView.text = joueur.note
            noteTextView.gravity = Gravity.CENTER

            // Appliquez le drawable de bordure aux TextView des joueurs
            joueurTextView.background = playerBorder
            noteTextView.background = playerBorder

            // Ajoutez un identifiant unique à chaque joueur
            joueurTextView.setTag(R.id.joueur_id, joueur.id)
            noteTextView.setTag(R.id.joueur_id, joueur.id)

            joueurTextView.setOnClickListener {
                val joueurId:Int = joueurTextView.getTag(R.id.joueur_id) as Int

                // Créez un fragment Joueur avec l'identifiant du joueur
                val joueurFragment = JoueurFragment.newInstance(joueurId)

                // Remplacez le fragment actuel par le fragment Joueur
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainActivity, joueurFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            noteTextView.setOnClickListener {
                val joueurId = noteTextView.getTag(R.id.joueur_id) as Int

                // Créez un fragment Joueur avec l'identifiant du joueur
                val joueurFragment = JoueurFragment.newInstance(joueurId)

                // Remplacez le fragment actuel par le fragment Joueur
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mainActivity, joueurFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            // Ajoutez les TextView à la TableRow
            row.addView(joueurTextView, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(noteTextView, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))

            // Ajoutez la TableRow au TableLayout
            tableau.addView(row)
        }

        return view
    }
}
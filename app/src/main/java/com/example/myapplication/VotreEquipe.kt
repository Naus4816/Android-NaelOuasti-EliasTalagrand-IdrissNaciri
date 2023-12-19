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

class VotreEquipeFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_votre_equipe, container, false)
        val db = FirebaseFirestore.getInstance()

        // Récupérer la collection equipe_choisie
        val equipeChoisieCollection = db.collection("equipe_choisie")

        // Récupérer la collection joueur
        val joueurCollection = db.collection("joueur")

        // Récupérer le TableLayout
        val tableau: TableLayout = view.findViewById(R.id.tableLayout)

        // Récupérer le drawable pour la bordure des joueurs
        val playerBorder = resources.getDrawable(R.drawable.table_background)

        equipeChoisieCollection.addSnapshotListener { equipeSnapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (equipeSnapshot != null && !equipeSnapshot.isEmpty) {
                val equipeDocument = equipeSnapshot.documents[0]
                val equipeChoisieId = equipeDocument.getString("id_equipe") ?: ""
                // Utiliser le nom de l'équipe choisie pour filtrer les joueurs
                joueurCollection.whereEqualTo("id_equipe", equipeChoisieId)
                    .addSnapshotListener { joueurSnapshot, joueurError ->
                        if (joueurError != null) {
                            Log.w("Firestore", "Listen failed for joueurs.", joueurError)
                            return@addSnapshotListener
                        }

                        if (joueurSnapshot != null && !joueurSnapshot.isEmpty) {
                            val joueurs = mutableListOf<Joueur>()

                            // Ajout des joueurs depuis la BDD
                            for (document in joueurSnapshot) {
                                val id = document.id
                                val nom = document.getString("nom") ?: ""
                                val note = document.getString("note") ?: ""
                                val type = document.getString("type") ?: ""
                                val id_stat = document.getString("id_stat")?: ""
                                val id_stat_goal = document.getString("id_stat_goal")?: ""

                                val joueur = Joueur(id, nom, note, id_stat, id_stat_goal,type)
                                joueurs.add(joueur)
                            }

                            // Ajout des joueurs dans le TableLayout
                            for (joueur in joueurs) {
                                val row = TableRow(requireContext())
                                val joueurTextView = TextView(requireContext())
                                val noteTextView = TextView(requireContext())

                                joueurTextView.text = joueur.nom
                                joueurTextView.gravity = Gravity.CENTER

                                noteTextView.text = joueur.note
                                noteTextView.gravity = Gravity.CENTER

                                // Application du drawable de bordure aux TextView des joueurs
                                joueurTextView.background = playerBorder
                                noteTextView.background = playerBorder

                                joueurTextView.setOnClickListener {
                                    val joueurId: String = joueur.id
                                    Log.d("joueur.id","joueur.id:${joueur.id}")

                                    // Création d'un fragment Joueur avec l'identifiant du joueur
                                    val joueurFragment = JoueurFragment.newInstance(joueurId)

                                    // Remplacement du fragment actuel par le fragment Joueur
                                    val transaction =
                                        requireActivity().supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.mainActivity, joueurFragment)
                                    transaction.addToBackStack(null)
                                    transaction.commit()
                                }

                                noteTextView.setOnClickListener {
                                    val joueurId: String = joueur.id

                                    // Création d'un fragment Joueur avec l'identifiant du joueur
                                    val joueurFragment = JoueurFragment.newInstance(joueurId)

                                    // Remplacement du fragment actuel par le fragment Joueur
                                    val transaction =
                                        requireActivity().supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.mainActivity, joueurFragment)
                                    transaction.addToBackStack(null)
                                    transaction.commit()
                                }

                                // Ajout des TextView à la TableRow
                                row.addView(
                                    joueurTextView,
                                    TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                )
                                row.addView(
                                    noteTextView,
                                    TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                )

                                // Ajout de la TableRow au TableLayout
                                tableau.addView(row)
                            }
                        } else {
                            Log.d("Firestore", "Aucun joueur trouvé pour l'équipe choisie")
                        }
                    }
            } else {
                Log.d("Firestore", "Aucune équipe choisie trouvée")
            }
        }

        return view
    }
}

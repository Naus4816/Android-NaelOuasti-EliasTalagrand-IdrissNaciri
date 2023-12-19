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
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class Joueur(
    val id: String,
    val nom: String,
    val note: String,
    val id_stat: String,
    val id_stat_goal: String,
    val type: String,
)

class JoueurFragment : Fragment() {

    private var stats: Stats? = null
    private var stats_goal: Stats_goal? = null

    companion object {
        fun newInstance(joueurId: String): JoueurFragment {
            val fragment = JoueurFragment()
            val args = Bundle()
            args.putString("id", joueurId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_joueur, container, false)
        val joueurId = arguments?.getString("id") ?: ""
        val db = FirebaseFirestore.getInstance()
        val joueurRef = db.collection("joueur").document(joueurId)

        joueurRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Le document existe, vous pouvez extraire les données du joueur
                    val nom = documentSnapshot.getString("nom") ?: ""
                    val id_stat = documentSnapshot.getString("id_stat") ?: ""
                    val id_stat_goal = documentSnapshot.getString("id_stat_goal") ?: ""
                    val note = documentSnapshot.getString("note") ?: ""
                    val type = documentSnapshot.getString("type") ?: ""
                    getStatsFromCollection("stat", id_stat) { stats ->
                        getStatsGoalFromCollection("stat_goal", id_stat_goal) { stats_goal ->
                            // Mettez à jour le joueur avec les données de la base de données
                            val joueur = Joueur(joueurId, nom, note, id_stat, id_stat_goal, type)

                            // Vérification si l'objet Joueur est non nul et si id_stat_goal est égal à 0
                            if (joueur!!.id_stat_goal == "0") {
                                this.stats = stats
                                this.stats_goal = Stats_goal(0, 0, 0, 0, 0)
                            } else {
                                this.stats = Stats(0, 0, 0, 0, 0)
                                this.stats_goal = stats_goal
                            }
                            afficherJoueur(view, joueur, stats, stats_goal)
                        }
                    }
                } else {
                    Log.d("Firestore", "Document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
        return view
    }

    //Fonction pour afficher le joueur et ses stats
    private fun afficherJoueur(view: View,joueur: Joueur,stats: Stats, stats_goal: Stats_goal) {
        val myApp = requireActivity().application as MyApplication
        var joursRestants = myApp.Data.joursRestants
        var entrainementsRealises = myApp.Data.entrainementsRealises

        val tableLayout: TableLayout = view?.findViewById(R.id.tableLayout) ?: return@afficherJoueur

        // Ajout dynamique des joueuers
        addRowToTable("Nom", joueur.nom, tableLayout)
        addRowToTable("Note", joueur.note, tableLayout)
        addRowToTable("Type", joueur.type, tableLayout)
        if(joueur.id_stat_goal == "0") {
            addRowToTable("Dribble", stats.dribble.toString(), tableLayout)
            addRowToTable("Défense", stats.defense.toString(), tableLayout)
            addRowToTable("Passes", stats.passes.toString(), tableLayout)
            addRowToTable("Tir", stats.tir.toString(), tableLayout)
            addRowToTable("Vitesse", stats.vitesse.toString(), tableLayout)
        }
        else {
            addRowToTable("Dégagement", stats_goal.degagement.toString(), tableLayout)
            addRowToTable("Jeu de main", stats_goal.jeu_de_main.toString(), tableLayout)
            addRowToTable("Plongeon", stats_goal.plongeon.toString(), tableLayout)
            addRowToTable("Réflexes", stats_goal.reflexes.toString(), tableLayout)
            addRowToTable("Vitesse", stats_goal.vitesse.toString(), tableLayout)
        }
        val textViewEntrainementsRealises: TextView = view.findViewById(R.id.textViewEntrainementsRealises)
        val textViewJoursRestants: TextView = view.findViewById(R.id.textViewJoursRestants)

        textViewEntrainementsRealises.text = "Entrainements réalisés: $entrainementsRealises"
        textViewJoursRestants.text = "Jours restants: $joursRestants"

        // Récupération du bouton "Entraînement"
        if(joursRestants != 0)
        {
            val btnEntrainement: Button = view?.findViewById(R.id.btnEntrainement) ?: return@afficherJoueur

            if(joursRestants==0)
            {
                btnEntrainement.isEnabled=false
            }

            // Gestion du clic sur le bouton "Entraînement"
            btnEntrainement.setOnClickListener {

                entrainementsRealises += 1
                updateEntrainementsRealises(entrainementsRealises)

                tableLayout.removeViewAt(3) // Supprimez la ligne "Dribble"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Défense"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Passes"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Tir"
                tableLayout.removeViewAt(3) // Supprimez la ligne "Vitesse"
                // Mettre à jour les statistiques de manière aléatoire
                if(joueur.id_stat_goal=="0")
                {
                    stats.updateStatsRandomly(joueur.id_stat)
                    addRowToTable("Dribble", stats.dribble.toString(), tableLayout)
                    addRowToTable("Défense", stats.defense.toString(), tableLayout)
                    addRowToTable("Passes", stats.passes.toString(), tableLayout)
                    addRowToTable("Tir", stats.tir.toString(), tableLayout)
                    addRowToTable("Vitesse", stats.vitesse.toString(), tableLayout)
                }
                else
                {
                    stats_goal.updateStatsGoalRandomly(joueur.id_stat_goal)
                    addRowToTable("Dégagement", stats_goal.degagement.toString(), tableLayout)
                    addRowToTable("Jeu de main", stats_goal.jeu_de_main.toString(), tableLayout)
                    addRowToTable("Plongeon", stats_goal.plongeon.toString(), tableLayout)
                    addRowToTable("Réflexes", stats_goal.reflexes.toString(), tableLayout)
                    addRowToTable("Vitesse", stats_goal.vitesse.toString(), tableLayout)
                }
                if (entrainementsRealises > 2)
                {

                    entrainementsRealises = 0
                    joursRestants--
                    updateJoursRestants(joursRestants)
                }
                textViewEntrainementsRealises.text = "Entrainements réalisés: $entrainementsRealises"
                textViewJoursRestants.text = "Jours restants: $joursRestants"

                // Désactivation du bouton s'il n'y a plus de jours restants
                if (joursRestants <= 0) {
                    updateEntrainementsRealises(entrainementsRealises)
                    btnEntrainement.isEnabled = false
                }

            }
        }
    }


    // Fonction pour récupérer les statistiques à partir de la collection appropriée
    private fun getStatsFromCollection(
        collectionName: String,
        statsId: String,
        callback: (Stats) -> Unit
    ) {
        val statsEnCoursCollection = FirebaseFirestore.getInstance().collection("stats_en_cours")
        val statsDocument = statsEnCoursCollection.document(statsId)

        statsDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Les stats existent dans stats_en_cours, récupérez-les
                    val dribble = documentSnapshot.getLong("dribble")?.toInt() ?: 0
                    val defense = documentSnapshot.getLong("défense")?.toInt() ?: 0
                    val passes = documentSnapshot.getLong("passe")?.toInt() ?: 0
                    val tir = documentSnapshot.getLong("tir")?.toInt() ?: 0
                    val vitesse = documentSnapshot.getLong("vitesse")?.toInt() ?: 0
                    callback(Stats(dribble, defense, passes, tir, vitesse))
                } else {
                    // Les stats n'existent pas dans stats_en_cours, chargez depuis la collection spécifiée
                    getStatsFromCollectionFallback(collectionName, statsId, callback)
                }
            } else {
                // Gérer les erreurs ici
                callback(Stats(0, 0, 0, 0, 0))
            }
        }
    }

    // Fonction pour récupérer les statistiques goal à partir de la collection appropriée
    private fun getStatsGoalFromCollection(
        collectionName: String,
        statGoalId: String,
        callback: (Stats_goal) -> Unit
    ) {
        val statsEnCoursGoalCollection = FirebaseFirestore.getInstance().collection("stats_en_cours_goal")
        val statsGoalDocument = statsEnCoursGoalCollection.document(statGoalId)

        statsGoalDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Les stats existent dans stats_en_cours, récupérez-les
                    val degagement = documentSnapshot.getLong("dégagement")?.toInt() ?: 0
                    val jeuDeMain = documentSnapshot.getLong("jeu_de_main")?.toInt() ?: 0
                    val plongeon = documentSnapshot.getLong("plongeon")?.toInt() ?: 0
                    val reflexes = documentSnapshot.getLong("réflexes")?.toInt() ?: 0
                    val vitesse = documentSnapshot.getLong("vitesse")?.toInt() ?: 0
                    callback(Stats_goal(degagement, jeuDeMain, plongeon, reflexes, vitesse))
                } else {
                    // Les stats n'existent pas dans stats_en_cours, chargez depuis la collection spécifiée
                    getStatsGoalFromCollectionFallback(collectionName, statGoalId, callback)
                }
            } else {
                // Gérer les erreurs ici
                callback(Stats_goal(0, 0, 0, 0, 0))
            }
        }
    }

    // Fonction de secours pour récupérer les statistiques depuis la collection spécifiée
    private fun getStatsFromCollectionFallback(
        collectionName: String,
        statId: String,
        callback: (Stats) -> Unit
    ) {
        val statsDocument = FirebaseFirestore.getInstance().collection(collectionName).document(statId)
        statsDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val dribble = documentSnapshot.getString("dribble")?.toInt() ?: 0
                    val defense = documentSnapshot.getString("défense")?.toInt() ?: 0
                    val passes = documentSnapshot.getString("passe")?.toInt() ?: 0
                    val tir = documentSnapshot.getString("tir")?.toInt() ?: 0
                    val vitesse = documentSnapshot.getString("vitesse")?.toInt() ?: 0
                    callback(Stats(dribble, defense, passes, tir, vitesse))
                } else {
                    callback(Stats(0, 0, 0, 0, 0)) // Valeurs par défaut si le document n'existe pas
                }
            } else {
                // Gérer les erreurs ici
                callback(Stats(0, 0, 0, 0, 0))
            }
        }
    }

    // Fonction de secours pour récupérer les statistiques goal depuis la collection spécifiée
    private fun getStatsGoalFromCollectionFallback(
        collectionName: String,
        statGoalId: String,
        callback: (Stats_goal) -> Unit
    ) {
        val statsGoalDocument = FirebaseFirestore.getInstance().collection(collectionName).document(statGoalId)
        statsGoalDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val degagement = documentSnapshot.getString("dégagement")?.toInt() ?: 0
                    val jeuDeMain = documentSnapshot.getString("jeu_de_main")?.toInt() ?: 0
                    val plongeon = documentSnapshot.getString("plongeon")?.toInt() ?: 0
                    val reflexes = documentSnapshot.getString("réflexes")?.toInt() ?: 0
                    val vitesse = documentSnapshot.getString("vitesse")?.toInt() ?: 0
                    callback(Stats_goal(degagement, jeuDeMain, plongeon, reflexes, vitesse))
                } else {
                    callback(Stats_goal(0, 0, 0, 0, 0)) // Valeurs par défaut si le document n'existe pas
                }
            } else {
                // Gérer les erreurs ici
                callback(Stats_goal(0, 0, 0, 0, 0))
            }
        }
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
    fun Stats.updateStatsRandomly(idStat: String) {
        dribble += Random.nextInt(0, 2)
        defense += Random.nextInt(0, 2)
        passes += Random.nextInt(0, 2)
        tir += Random.nextInt(0, 2)
        vitesse += Random.nextInt(0, 2)

        val updatedStats = mapOf(
            "dribble" to dribble,
            "défense" to defense,
            "passe" to passes,
            "tir" to tir,
            "vitesse" to vitesse
        )

        updateStatsEnCours(idStat,updatedStats)

    }

    fun Stats_goal.updateStatsGoalRandomly(idStat: String) {
        degagement += Random.nextInt(0, 2)
        jeu_de_main += Random.nextInt(0, 2)
        plongeon += Random.nextInt(0, 2)
        reflexes += Random.nextInt(0, 2)
        vitesse += Random.nextInt(0, 2)

        val updatedStats = mapOf(
            "dégagement" to degagement,
            "jeu_de_main" to jeu_de_main,
            "plongeon" to plongeon,
            "réflexes" to reflexes,
            "vitesse" to vitesse
        )

        updateStatsEnCoursGoal(idStat,updatedStats)
    }

    private fun updateJoursRestants(joursRestants: Int) {
        val myApp = requireActivity().application as MyApplication
        myApp.Data.updateJoursRestants(joursRestants)
    }

    private fun updateEntrainementsRealises(entrainementsRealises: Int) {
        val myApp = requireActivity().application as MyApplication
        myApp.Data.updateEntrainementsRealises(entrainementsRealises)
    }

    fun updateStatsEnCours(idStat: String, statsToUpdate: Map<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        val statsEnCoursCollection = db.collection("stats_en_cours")

        // Créer une requête pour trouver le document avec le champ spécifié
        val query = statsEnCoursCollection.whereEqualTo("id_stat", idStat)

        // Exécuter la requête
        query.get()
            .addOnSuccessListener { querySnapshot ->
                // Vérifier si des documents correspondent à la requête
                if (!querySnapshot.isEmpty) {
                    // Mettre à jour le premier document correspondant
                    val docRef = querySnapshot.documents[0].reference

                    // Effectuer la mise à jour du document
                    docRef.update(statsToUpdate)
                        .addOnSuccessListener {
                            // La mise à jour a réussi
                            Log.d("Firestore", "Mise à jour réussie pour id_stat=$idStat")
                        }
                        .addOnFailureListener { e ->
                            // La mise à jour a échoué
                            Log.d("Firestore", "Erreur lors de la mise à jour pour id_stat=$idStat : $e")
                        }
                } else {
                    // Aucun document trouvé, ajouter un nouveau document avec les stats
                    statsEnCoursCollection.document(idStat)
                        .set(statsToUpdate)
                        .addOnSuccessListener {
                            // Ajout réussi
                            Log.d("Firestore", "Ajout réussi pour id_stat=$idStat")
                        }
                        .addOnFailureListener { e ->
                            // L'ajout a échoué
                            Log.d("Firestore", "Erreur lors de l'ajout pour id_stat=$idStat : $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                // La requête a échoué
                Log.d("Firestore", "Erreur lors de la requête : $e")
            }
    }

    fun updateStatsEnCoursGoal(idStat: String, statsToUpdate: Map<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        val statsEnCoursGoalCollection = db.collection("stats_en_cours_goal")

        // Créer une requête pour trouver le document avec le champ spécifié
        val query = statsEnCoursGoalCollection.whereEqualTo("id_stat", idStat)

        // Exécuter la requête
        query.get()
            .addOnSuccessListener { querySnapshot ->
                // Vérifier si des documents correspondent à la requête
                if (!querySnapshot.isEmpty) {
                    // Mettre à jour le premier document correspondant
                    val docRef = querySnapshot.documents[0].reference

                    // Effectuer la mise à jour du document
                    docRef.update(statsToUpdate)
                        .addOnSuccessListener {
                            // La mise à jour a réussi
                            Log.d("Firestore", "Mise à jour réussie pour id_stat=$idStat")
                        }
                        .addOnFailureListener { e ->
                            // La mise à jour a échoué
                            Log.d("Firestore", "Erreur lors de la mise à jour pour id_stat=$idStat : $e")
                        }
                } else {
                    // Aucun document trouvé, ajouter un nouveau document avec les stats
                    statsEnCoursGoalCollection.document(idStat)
                        .set(statsToUpdate)
                        .addOnSuccessListener {
                            // Ajout réussi
                            Log.d("Firestore", "Ajout réussi pour id_stat=$idStat")
                        }
                        .addOnFailureListener { e ->
                            // L'ajout a échoué
                            Log.d("Firestore", "Erreur lors de l'ajout pour id_stat=$idStat : $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                // La requête a échoué
                Log.d("Firestore", "Erreur lors de la requête : $e")
            }
    }
}

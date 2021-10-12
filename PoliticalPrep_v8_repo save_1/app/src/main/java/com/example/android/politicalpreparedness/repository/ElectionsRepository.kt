package com.example.android.politicalpreparedness.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Concrete implementation of a data source as a database.
 * @param database the database should link to the DAO
 */
class ElectionsRepository(private val database: ElectionDatabase) {

    /**
     * Get the elections list from the local db, using the DAO. Its possibly a long running task so use dispatchers.IO
     */

    //get all elections
 //val allElections: LiveData<DatabaseElection> = Transformations.map(database.electionDao.getSelectedElection()) {     //Transformations.map(database.electionDao.getAllElections()) {
     //it.asDomainModel()  }

    //followed elections
  // val followedElections: LiveData<List<Election>> = Transformations.map(database.electionDao.getElection()) {
  //     it.asDomainModel()
   // }

    //create a refresh function for the adapter
    //call data via network using retrofit, store elections received in a variable - results
    //

    suspend fun refreshElectionsList() {
        withContext(Dispatchers.IO) {
            try {
                //retrofit Json String - try electionReponse to retrieve data
                val elecResponses = CivicsApi.retrofitService.getElectionResults()  //
                val results = elecResponses.elections


                //transfer elections to the database
              //  database.electionDao.insertAllElections(*result.toTypedArray()) //need to check on this
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    //this is getting the saved details from the repo
    suspend fun getElections() {
        withContext(Dispatchers.IO) {
            try {       //get election from the db
                     database.electionDao.getElection()     //databaseDao.getElection()
                // databaseDao.getAllElections()   //     .electionDao.getElection()
            } catch (e: Exception) {   //if there is a fault then print the message
                Log.e("Election issue", "Election not found: " + e.message)
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(election: Election) {
       // val followElection =
        //databaseDao.insert(election)
        database.electionDao.insert(election)
    }





}
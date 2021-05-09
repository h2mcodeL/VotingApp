package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//TO DO: Construct ViewModel and provide election datasource

//this viewmodel header has parameters database and the application
class ElectionsViewModel(private val database: ElectionDao, application: Application): AndroidViewModel(application) {

    //TO DO: Create live data val for upcoming elections
        private var upcomingelections = MutableLiveData<Election?>()        //mutableLiveData for the election
        val elections = database.getAllElections()                          //like back to the database and getall elections


    //use these for the google civics
    private val _properties = MutableLiveData<List<Election>>()        //i dont think the type is election, but ElectionProperty
    val properties: LiveData<List<Election>>       //<String>> this is for hte recycler view
    get() = _properties
    /** Converted elections to Spanned for displaying */
    //  val electionString = Transformations.map(elections) { elections ->
    //    formatElections(elections, application.resources)
    //}

    //use this for testing the network connection
    private val _response = MutableLiveData<String>()

    val response: LiveData<String>
    get() = _response

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
    get() = _status


    //TODO: Create live data val for saved elections



    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info


    private val _navigateToVoterInfo = MutableLiveData<Int>()      //<Election>()
    val navigateToVoterInfo
    get() = _navigateToVoterInfo

    fun onElectionClicked(id: Int) {
        _navigateToVoterInfo.value = id
    }


    init {
       // initializeElection()
        //this is a temp solution
        getElectionProperties()
    }

    private fun initializeElection() {
        viewModelScope.launch {
            upcomingelections.value = getElectionsDatabase()
            getElectionProperties()
        }
    }

    //get all items from the elections database
    private suspend fun getElectionsDatabase(): Election? {
        return database.getElection()       //this uses inline
        /*var election = database.getElection()
        return election*/
    }


    private fun getElectionProperties() {
        viewModelScope.launch {

            val getElections = CivicsApi.retrofitService.getElectionResults()

            try {
         //       val result = getElections.elections
                //i need to get this value as a list of elections. shown in the election_item_view
                _properties.value = getElections.elections
                Log.i("LIST OF ELECTIONS", "List of items: ${listOf(_properties)}")
            } catch (e: Exception) {
                Log.i("ERROR MESSAGE", "Could not refresh the election list: ${e.message}")
                 // _properties.value = e.message
            }
        }
    }


    //navigation for the adapater
    fun displayElectionsDetails(election: Election) {
        _navigateToVoterInfo.value = 1
    }

    //database functions

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }

     suspend fun update(election: Election) {
         withContext(Dispatchers.IO) {
             database.update(election)
         }
     }

        suspend fun insert(election: Election) {
            withContext(Dispatchers.IO) {
                database.insert(election)
            }
        }
    }
}
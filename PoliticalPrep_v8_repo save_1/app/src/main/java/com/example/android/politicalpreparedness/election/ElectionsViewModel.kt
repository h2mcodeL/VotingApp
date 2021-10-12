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
import kotlinx.coroutines.launch


//TO DO: Construct ViewModel and provide election datasource
//use data injection to access database.

class ElectionsViewModel(
        val database: ElectionDao, application: Application) : AndroidViewModel(application) {

    // private val database = ElectionDatabase.getInstance(application) //get access to the database conflict with above, this is done already

    //create an instance of election
    private var userelection = MutableLiveData<Election?>()

    val elections = database.getAllElections()

    //create the repository
   // ----- private val electionsRepository = ElectionsRepository(database)

    //use these for the google civics
    private val _upcomingElections = MutableLiveData<List<Election?>>()        //i dont think the type is election, but ElectionProperty
    val upcomingElections: LiveData<List<Election?>>
        get() = _upcomingElections

    /** Converted elections to Spanned for displaying */
    //  val electionString = Transformations.map(elections) { elections ->
    //    formatElections(elections, application.resources)
    //}

    //use this for testing the network connection
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response


    //TO DO: Create live data val for saved elections

    //  private val _saved_election = MutableLiveData<Election>() //check this code
    //  val saved_election: LiveData<Election>
    //  get() = _saved_election


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database


    //TO DO: Create functions to navigate to saved or upcoming election voter info
    //upcoming elections come from getElections

    //navigate to the selected voterinfo from the election mutablelivedata externalized as livedata, links to display below
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    //this is for hte saved elections
    private val _savedElection = MutableLiveData<Election>()
    val savedElection: MutableLiveData<Election>
        get() = _savedElection


//    //create livedata for the election to follow
//    private val _followElection = MutableLiveData<Election>()
//    val followElection: LiveData<List<Election>>
//        get() = electionsRepository.followedElections


    //election is initialised from Retrofit Service
    init {
        getElections()
       // initializeFollowElection()      //this is to set up the FollowElection coroutines.
    }


    //here we get live election details. no need to go into the repo, use the repo for the saved electrions
    private fun getElections() {
        viewModelScope.launch {
            try {
                val getElections = CivicsApi.retrofitService.getElectionResults()
                val listResult = getElections.elections
                _upcomingElections.value = listResult

                _response.value = "Success: $getElections.size number of elections"

                Log.i("UPCOMING ELECTIONS", "${_upcomingElections.value}")
                //this log message lists the elections to be shown in recycler list
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message} Items listed"
            }
        }
    }

    //this allows navigation to the voter info screen from _naviga.... above
    fun displayElectionDetails(election: Election) {
        _navigateToVoterInfo.value = election
    }

    //clean up the navigation
    fun displayElectionDetailsComplete() {
        _navigateToVoterInfo.value = null
    }

//    SAVED ELECTIONS
//    private fun initializeFollowElection() {
//        viewModelScope.launch {
//            userelection.value = getFollowElection()
//        }
//    }

//    private suspend fun getFollowElection(): Election? {
//        return withContext(Dispatchers.IO) {
//            //var election = database.getElection()
//         //if (election?.id != null) {
//         //   election = null
//      //  }
//       // return election
//       // return database.getElection()
//            return@withContext database.getElection()   //this is inline
//    }
//    }

    suspend fun clear() {
        database.clear()
    }

//    suspend fun update(election: Election) {
//        database.insert(election)
//    }
//
//    suspend fun insert(election: Election) {
//        database.insert(election)
//    }


//   fun displaySavedDetails(election: Election) {
//        _savedElection.value = election
//    }
//
//    fun displaySavedDetailsComplete() {
//        _savedElection.value = null
//    }

////
////    //implement the follow function
//    fun followSelectedElection() {
//        viewModelScope.launch {
//            val newElection = Election()       //the elections was looking for id value.
//            insert(newElection)
//            userelection.value = getFollowElection()
//        }
//    }
//
//
//
//
//    //database functions
//    suspend fun insert(election: Election) {
//        withContext(Dispatchers.IO) {
//            database.insert(election)
//        }
//    }
//
//    private suspend fun clear() {
//        withContext(Dispatchers.IO) {
//            database.clear()
//        }
//
//        suspend fun update(election: Election) {
//            withContext(Dispatchers.IO) {
//                database.update(election)
//            }
//        }
//    }
}
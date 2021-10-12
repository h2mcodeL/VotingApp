 package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

 class VoterInfoViewModel(private val database: ElectionDao,
                          private val electionId: Int,
                          private val division: Division) : ViewModel() {

     //create an instance of election, for use within VoterInfo
     private var currentElection = MutableLiveData<Election?>()         //this is the current election

     val elections = database.getAllElections()

     //convert the elections
//     val electionString = Transformations.map(elections) { election ->
//         formatElections(election, application.resources)
//     }

     /**as we are using the repository to get the data, we will use databse - get access to database */

     //create an election variable, starting at null as the db may be empty
     var electionSaved: Election? = null

     //this is for the follow button
     var showLoading: Boolean = false

     //TO DO: Add live data to hold voter info, this is from initialize below
     private val _selectedVoterInfo = MutableLiveData<VoterInfoResponse>()
     val selectedVoterInfo: LiveData<VoterInfoResponse>
         get() = _selectedVoterInfo


     private val _savedElection = MutableLiveData<Election>()
     val savedElection: LiveData<Election>
         get() = _savedElection

     //this is used for the unfollow election
     private val _removeElection = MutableLiveData<Election>()
     val removeElection: LiveData<Election>
         get() = _removeElection

     private val _navigateVoter = MutableLiveData<Election>()
     val navigateVoter: LiveData<Election>
         get() = _navigateVoter

 //reset the navigation variable
 fun doneNavigating() {
    _navigateVoter.value = null

 }

     private val electionListObserver = Observer<Election> {
         _savedElection.value = it
     }

     //  private var electionLiveData: LiveData<Election>

     //TODO: Add var and methods to populate voter info
     //this is for the screen with the follow button

     init {
         getInfo()
         initializeFollowElection()
     }

     private fun getInfo() {
         viewModelScope.launch {
             var address = "country:${division.country}"
             address += if (division.state.isNotBlank() && division.state.isNotEmpty()) {      //if the state from Division is not blank and not empty
                 "/state:${division.state}"
             } else {
                 "/state:ca"     //this is a place holder
             }
             _selectedVoterInfo.value = CivicsApi.retrofitService.getVoterInfo(     //now take the livedata and get the address and the election Id.
                     address, electionId)
         }
         Log.i("This is the Voter Info", "$currentElection")
     }


     private val _votingLocations = MutableLiveData<String?>()   //this is for the url so use a string
     val votingLocations: LiveData<String?>
         get() = _votingLocations

     //set up click function for voting locations
     //take the voterLocation livedata and assign it to the location url
     fun votingLocationsClick() {
         _votingLocations.value = _selectedVoterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl

     }

     fun voterLocationNavigate() {
         _votingLocations.value = null
     }

     private fun initializeFollowElection() {
         viewModelScope.launch {
             currentElection.value = getFollowElection()
         }
     }

     private suspend fun getFollowElection(): Election? {
         return withContext(Dispatchers.IO) {
             //val election = database.getSelectedElection(electionId)
             val election = database.getElection()
             if (election?.id == null)
                 Log.i("Error Message", "No election provided")

             //return database.getSelectedElection(electionId)
             return@withContext election
         }
     }

     //implement the follow function
     fun followSelectedElection() {
         viewModelScope.launch {
             val newElection = Election(
                     id = electionSaved!!.id,
                     name = electionSaved!!.name,
                     division = electionSaved!!.division,
                     electionDay = electionSaved!!.electionDay)
             insert(newElection)
             currentElection.value = getFollowElection()

         }
     }

     //database functions
     private suspend fun insert(election: Election) {
         withContext(Dispatchers.IO) {
             database.insert(election)
         }
     }


/*
     private fun refreshData() {
         viewModelScope.launch {
             electionRepository.refreshElectionsList()
         }
     } */

//     override fun onCleared() {
//         super.onCleared()
//       //  electionLiveData.removeObserver(electionListObserver)
//     }

     private suspend fun clear() {
         withContext(Dispatchers.IO) {
             database.clear()
         }

 }
         }










     /*


     //------------------------------------------------------------------------------------

     //TODO: Populate voter info -- hide views without provided data.
     /**
     Hint: You will need to ensure proper data is provided from previous fragment.
      */


     //create a function to validate the entered election to follow
     fun validateAndFollowElection(electionData: DataSource) : Boolean {
         if(validateEnteredElection(electionData)) {
         //    followElection(electionData)
             return true
         }
         return false
     }


//-------------------------- new data ----------------------------

     //SAVED ELECTIONS
     //create a couroutine for the followed election, which is a long running routine
     private fun initializeFollowElection() {
         viewModelScope.launch {
             currentElection.value = getFollowElection()
         }
     }

     private suspend fun getFollowElection(): Election? {
         /*  var election = database.getElection()
           if (election?.id != null) {
               election = null
           }
           return election

         return database.getElection()
     }

  */

     //define the insert function

     fun displaySavedDetails(election: Election) {
         _savedElection.value = election
     }

     fun displaySavedDetailsComplete() {
         _savedElection.value = null
     }


     //implement the follow function
     fun followSelectedElection() {
         viewModelScope.launch {
             val newElection = Election(
                     id = electionSaved!!.id,
                     name = electionSaved!!.name,
                     division = electionSaved!!.division,
                     electionDay = electionSaved!!.electionDay)
             insert(newElection)
             currentElection.value = getFollowElection()
         }
     }




     //database functions
     suspend fun insert(election: Election) {
         withContext(Dispatchers.IO) {
             database.insert(election)
        }
     }

     private suspend fun clear() {
         withContext(Dispatchers.IO) {
             database.clear()
         }

         suspend fun update(election: Election) {
             withContext(Dispatchers.IO) {
                 database.update(election)
             }
         }
     }
//-------------------------- new data end

//civics info api - voterInfo requires as inputs 'address', and the 'electionId' which is passed over from election
// the address can simply be a state and not a full address


     /*
//when we initialise VoterInfo, we start by setting the voter value to the function get from database
 private fun initializeVoterInfo() {
    viewModelScope.launch {     //launching coroutine, prevent thread from blocking
     //create an address variable, check that it exists
      var address = division.country     //create a variable for the address item division = country
      /*try this*/ //  var address = "country:${division.country}"
        if (!division.state.isBlank() && !division.state.isEmpty()) { //check if the address is not blank and not empty,
            address += "/state:${division.state}"       //append address
        } else {        //use this as a place holder
            address += "/state:al"
        }
        //now assign the value of _selectedElection to the output
        _selectedVoterInfo.value = CivicsApi.retrofitService.getVoterInfo(
                address, electionId)

        Log.i("Show the output", "${electionId}")
    }



         //this gives us - division.country, division.state
 }


     //     //use the repo to get access to db functions...
//   private suspend fun follow() {
//        viewModelScope.launch {
//         //   electionsRepo.getElections()
//            Log.i("Simple Log message","Simple")
//        }
//    }


    //this is a long running task, so we use a coroutine
     //tis gets the election from the database
     //private suspend fun getElectionFromDatabase(): Election? {

 /*private suspend fun getVoterInfoFrom(): Election? {
         var election = database.getElection()      //should this use the election ID
         return election
     }

  */


     //here we use a viewmodelscope to get access to the retrofit data download from google civics
     //


//     private fun getVoterInfo() {
//         viewModelScope.launch {
//             try {
//                 val getVotersInfo = CivicsApi.retrofitService.getVoterInfo(voter, address)        //CivicsApi.retrofitService.getElectionResults()
//                 val voterInfoResult = getVotersInfo.election
//                 _selectedElection.value = voterInfoResult
//
//                 _response.value = "Success: $getVotersInfo.size number of voters"
//                 _response.value = "${getVotersInfo.election}"
//             } catch (e: Exception) {
//                 _response.value = "Failure: ${e.message}"
//             }
//         }
//     }









    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database

    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
      */
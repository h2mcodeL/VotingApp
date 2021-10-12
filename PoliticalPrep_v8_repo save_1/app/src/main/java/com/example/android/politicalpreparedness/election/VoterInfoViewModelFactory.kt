package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Division

//we require dataSource, electionId, division
class VoterInfoViewModelFactory(
        private val dataSource: ElectionDao,
      //  private val dataSource: ElectionDatabase,
       // private val election: Election,
        private val electionId: Int,
        private val division: Division,
        private val application: Application): ViewModelProvider.Factory {
        //private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(dataSource, electionId, division/*, application*/) as T       //(dataSource, electionId, division/*, application*/) as T
        }
        throw IllegalArgumentException("Unable to create VoterInfoViewModel")
    }
}

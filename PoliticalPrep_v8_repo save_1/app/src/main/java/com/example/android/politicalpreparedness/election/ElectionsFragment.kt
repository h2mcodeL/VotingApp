package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //TO DO: Declare ViewModel
    private lateinit var electViewModel: ElectionsViewModel
    private lateinit var binding: FragmentElectionBinding

   // private lateinit var savedElections: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View/*?*/ {

        //TO DO: Add binding values
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_election, container, false)

        //get an instance of the application for use in the viewModelFactory
        val application = requireNotNull(this.activity).application
       val dataSource = ElectionDatabase.getInstance(application).electionDao  //do i need this here
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)       //using factory helps with constructing the viewmodel

        //TO DO: Add ViewModel values and create ViewModel  (get a reference to the viewmodel associated with this fragment using providers
        electViewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        //this binds the name election from the xml file directly with the viewmodel
        binding.electionViewModel = electViewModel

        //binding observes livedata updates
        binding.lifecycleOwner = this

        //TO DO: Link elections to voter info --- voter info is simply the it

        
       // val adapter = ElectionListAdapter(ElectionListAdapter.OnClickListener())
       // binding.upcomingElectionsList.adapter = adapter


        val manager = LinearLayoutManager(activity)
        binding.upcomingElectionsList.layoutManager = manager

        //TO DO: Initiate recycler adapters. We use the binding to link the adapter to the
        binding.upcomingElectionsList.adapter = ElectionListAdapter(ElectionListAdapter.OnClickListener {
            //electionId -> Toast.makeText(context, "$electionId", Toast.LENGTH_SHORT).show() ///change this for the details screen

            electViewModel.displayElectionDetails(it)
            Log.i("List of Items", "${electViewModel.elections}")
        })


        //this is used to navigate to the upcoming elections linking elections to voter info
        //and pass on the election id
        electViewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            if  (null != it) {
                this.findNavController().navigate(ElectionsFragmentDirections.electionFragmentToVoterInfoFragment(it.id, it.division))
                electViewModel.displayElectionDetailsComplete()
            }
        })

        //set up the SAVED election list
      //---  binding.savedElectionsList.adapter = ElectionListAdapter(ElectionListAdapter.OnClickListener {
      //---      electViewModel.displaySavedDetails(it)
     //---   })

//      electViewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
//          if (null != it) {
//              this.findNavController().navigate( )
//          }
//      })
        /**
        binding.savedElectionsList =
        savedElections = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                    ElectionsFragmentDirections.electionFragmentToVoterInfoFragment(it.id, it.division)
            )
        })
        */

        //TO DO: Populate recycler adapters  ---// this is done above
        //go to fragment voter info, use FOLLOW button to save selection.
        //  return inflater.inflate(R.layout.fragment_election, container, false)
        return binding.root

    //}

    //TODO: Refresh adapters when fragment loads
      //  binding.refreshLayout.setOnRefreshListener {viewModel.loadElections()}

}

}
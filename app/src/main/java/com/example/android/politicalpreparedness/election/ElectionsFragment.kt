package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //TO DO: Declare ViewModel
    private lateinit var electViewModel: ElectionsViewModel

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_election, container, false)

        //get an instance of the application
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)

        //TO DO: Add ViewModel values and create ViewModel  (get a reference to the viewmodel associated with this fragment
        //electionViewModel = ViewModelProvider(this/*, viewModelFactory*/).get(ElectionsViewModel::class.java)


        electViewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)       //the text this, tells us the associated Fragment/activity

        //this binds the name election from the xml file to the viewmodel
        //binding.electionViewModel = electionViewModel as ElectionsViewModel
        binding.electionViewModel = electViewModel

        //binding observes livedata updates
        binding.lifecycleOwner = this



        //TODO: Link elections to voter info


        //we use the adappter to get the ListAdapter

        binding.upcomingElectionsList.adapter = ElectionListAdapter(ElectionListAdapter.OnClickListener {
            electViewModel.properties
        })

//        electViewModel.properties.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                adapter.submitList(it)
//            }
//        })

        //TO DO: Initiate recycler adapters.... what is this>>????
//        val adapter = ElectionListAdapter(ElectionListAdapter.OnClickListener {
//            electionId -> Toast.makeText(context, "$electionId", Toast.LENGTH_SHORT).show()
//        })
      //  binding.upcomingElectionsList.adapter = adapter

       // we use the adapter to get the listAdapter
       // binding.upcomingElectionsList.adapter = ElectionListAdapter() {
       //     electionViewModel.displayUpcomingElections(it)
       // }


        //connect with the viewmodel
//        electViewModel.elections.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                adapter.submitList(it)      //submitList is a method in ListAdapter
//            }
//        })


        //TODO: Populate recycler adapters  ---// this is done above
        //electionViewModel



      //  return inflater.inflate(R.layout.fragment_election, container, false)
        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}
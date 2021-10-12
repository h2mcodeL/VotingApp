package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel : VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //reference to the application that this fragment is attached to
        val application = requireNotNull(this.activity).application //throws an illegal if argument is null

        //using this method we bind directly to the view using the binding reference
        val binding = FragmentVoterInfoBinding.inflate(inflater)

      // ---------  binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        //the current activity is the lifecycle owner of the binding.
        binding.lifecycleOwner = this

       //TO DO: Add ViewModel values and create ViewModel
        val dataSource = ElectionDatabase.getInstance(application).electionDao  //this gives access to teh DAO
      //  val dataSource = ElectionDatabase.getInstance(application)

        //voterInfoQuery requires electionId and an address

        //we store our data in a bundle rather then separate
       // val election = VoterInfoFragmentArgs.fromBundle(arguments!!).selectedElection
        val bundle = VoterInfoFragmentArgs.fromBundle(requireArguments())      //were using navigation safeArgs
        val electionId = bundle.argElectionId
        val division = bundle.argDivision

        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionId, division, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

       // binding.stateLocations.setOnClickListener {
         //   viewModel.votingLocationsClick()
       // }

        //handle url loading
        viewModel.votingLocations.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadUrl(it)     //call the url method
                viewModel.voterLocationNavigate()   //clean up the navigation
            }
        })

//        viewModel.navigateVoter.observe(viewLifecycleOwner, Observer {
//            election ->
//            election?.let {
//                this.findNavController().navigate(
//                        VoterInfoFragmentDirections
//
//                )
//            }
//
//        })


        //TO DO: Add binding values

        //these are the binding values from the voterInfo layout, these go in the viewmodel
//        var electionName = binding.electionName
//        var electionDate = binding.electionDate
//        val stateHeader = binding.stateHeader
//        val stateLocations = binding.stateLocations
//        val stateBallot = binding.stateBallot
//        val stateCorrespondence = binding.stateCorrespondenceHeader


        //use data binding to access viewmodel in the xml file, instantiate the voterviewmodel
       // binding.viewModel = viewModel         //voterViewModel

       //we need to get the election id from the electionFragment for use here
       //to use safeArgs, we need to add the arguments to the navGraph...
       // val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId   ----- refer to above


        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        //check if this is done here or in the viewmodel



        //TODO: Handle loading of URLs
        //which urls need to be loaded here - below

        //TODO: Handle save button UI state     -- ###### refer to the loading button app
        //TODO: cont'd Handle save button clicks

        //im looking for voter location and ballot information.

        return binding.root

    }

    //TO DO: Create method to load URL intents

    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
/** from the rep adapter
    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
    */

}

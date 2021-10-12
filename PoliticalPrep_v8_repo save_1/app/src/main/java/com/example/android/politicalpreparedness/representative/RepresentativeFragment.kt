package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    //TO DO: Declare ViewModel      setup fields here
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel

    private lateinit var getLocation: LocationServices

    //create a object to store the election item
    private lateinit var electionItem: Election     //check if this is the correct structure

    //check if phone is running Q or later
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private lateinit var geofenceClient: GeofencingClient
  //  private val geofencePendingIntent: PendingIntent by lazy {
   //     val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
  //  }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TO DO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        //initialise viewmodel
        viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)

      //  setDisplayHomeAsUpEnabled(true)       //create function
      // set up binding to xml layout file
      binding.viewModel = viewModel

        //set up the adapter here, however the item should be in Activity below
      //  val adapter = RepresentativeListAdapter()
        //TO DO: Define and assign Representative adapter
        val repsAdapter = RepresentativeListAdapter()
        binding.representativeList.adapter = repsAdapter        //RepresentativeListAdapter()



        /**
        //TODO: Populate Representative adapter
       viewModel.navToRep.observe(viewLifecycleOwner, Observer {
        viewModel.displayRepsDetails()
       })
        */


        //TODO: Establish button listeners for field and location search

        return binding.root
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {

        //create the foreground and background permissions
        //if already created, assign to true then return permissions
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION))

        //dont need background permission
        val backgroundPermissionApproved =
                if (runningQOrLater) {
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(requireContext(),
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    true
                }
        return foregroundLocationApproved // && backgroundPermissionApproved
    }

    override fun onRequestPermissionsResult(

            //check the permissions result
            //if grantResults is empty, or denied show snack bar
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults)  check if this is needed
        //TO DO: Handle location permission result to get location on permission granted

        if (grantResults.isEmpty() ||
                        grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                                grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                                PackageManager.PERMISSION_DENIED))
        {
            Snackbar.make(
                    this.requireView(),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
            )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
        } else {
            Log.i("Request Permissions","Permission Check")
            //use this for now
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

        @TargetApi(29 )
        private fun requestForegroundAndBackgroundLocationPermissions() {
            if (foregroundAndBackgroundLocationPermissionApproved())
                return
            var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

            val resultCode = when {
                runningQOrLater -> {
                    permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
                }
                else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }

           Log.i("Request foreground only location permission", "Requesting foreground permission")
            requestPermissions(permissionsArray, resultCode)
        }

    //check settings and start i.e. geofence

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val locationSettingRequestsBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(locationSettingRequestsBuilder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                            exception.resolution.intentSender,
                            REQUEST_TURN_DEVICE_LOCATION_ON, null,
                            // REQUEST_CODE_LOCATION_SETTING, null,
                            0, 0, 0,
                            null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    //  Log.i("Error Message", sendEx.message)      //change this for Timber
                }
            } else {
                Snackbar.make(
                        this.requireView(),
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        //if location setting are successful, add geofence.
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                // Timber.i("Granted")

                //here we know the device location is enabled, now we can move onto adding the Geofence
             //   addGeofenceForReminder(reminderData)
            }
        }
    }


    private fun checkLocationPermissions(): Boolean {
      //  return if (isPermissionGranted()) {
       return if (foregroundAndBackgroundLocationPermissionApproved()) {
            getLocation()
            true
        } else {
            //TO DO: Request Location permissions
                requestForegroundAndBackgroundLocationPermissions()
            false
        }
    }


    private fun isPermissionGranted() : Boolean {
        //TO DO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
         if (foregroundAndBackgroundLocationPermissionApproved()) {
            getLocation()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
        return true
    }



    private fun getLocation() {
        //TO DO: Get location from LocationServices
        //TO DO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        if (isPermissionGranted()) {
            getLocation
        }
         else  {
             ActivityCompat.requestPermissions(
                     context as Activity,
                     arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                     REQUEST_LOCATION_PERMISSION
             )
            }
        //if (getLocation) {
        //    viewModel.address_line_1.value = getLocation.toString()
          //  viewModel.address_line_2.value = getLocation.toString()
          //  viewModel.city.value = getLocation.toString()
          //  viewModel.state.value = getLocation.toString()
            //viewModel.zip.value = getLocation.toString()

            //viewModel.navigationCommand.value = NavigationCommand.Back
            //}
      }


    //why do i have this???
    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    //this is for the address input
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    companion object {
        //TO DO: Add Constant for Location request - these are the foreground/background etc
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        private const val REQUEST_CODE_LOCATION_SETTING = 1   //need to check this
        //const val GEOFENCE_RADIUS_IN_METERS = 500f


        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}

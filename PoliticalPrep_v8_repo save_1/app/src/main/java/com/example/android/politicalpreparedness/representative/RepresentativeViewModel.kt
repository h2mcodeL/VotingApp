package com.example.android.politicalpreparedness.representative

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives


    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
    get() = _address

//    private val _navToRep = MutableLiveData<Representative>()
//    val navToRep: LiveData<Representative>
//    get() = _navToRep


    //use this for testing the network connection
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    //TO DO: Establish live data for representatives and address
    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<Int>()


    //TO DO: Create function to fetch representatives from API from a provided address

    fun getRepresentatives(address: String){
        viewModelScope.launch {
            val (office, officials) = CivicsApi.retrofitService.getRepresentatives(address)
        _representatives.value = office.flatMap { office -> office.getRepresentatives(officials)
        }
        }
    }
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :
     */
    /*
    private fun getReps() {
        viewModelScope.launch {
            try {
                //val (offices, officials) = getRepresentativesDeferred.await()
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives()
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message} "
            }
        }
    }*/


    //TO DO: Create function get address from geo location
    private fun getAddressFromGeoLocation(address: Address) {
        _address.value = address
    }

    init {
     //   _address.value = Address("", "", "", "New York", "")
    }



    //TODO: Create function to get address from individual fields
//     fun getAddress() {
//        viewModelScope.launch {
//        }
//         address.value = addressLine1.value
//     }


//   fun getAddressFromList(address) {
//        viewModelScope.launch {
//           try {
//               Address(
//                      address.addressLine1,
//                      address.addressline2,
//                      address.city,
//               )
//           }
//                val getRepresentatives = CivicsApi.retrofitService.getRepresentatives( /*  pass in address*/)
//               val listReps = getRepresentatives().
//          }
//       }
//   }



    fun onClear() {
        addressLine1.value = null
        addressLine2.value = null
        city.value = null
        state.value = null
        zip.value = null
    }
}

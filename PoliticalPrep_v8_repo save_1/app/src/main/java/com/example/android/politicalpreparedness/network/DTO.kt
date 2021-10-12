package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.squareup.moshi.JsonClass
import java.util.*


/**
        Election represents an item that can played
 */


@JsonClass(generateAdapter = true)
data class NetworkElectionContainer(val election: Election)


@JsonClass(generateAdapter = true)
data class ElectionItem(
        val id: Int,
        val name: String,
        val electionDate: Date,
        val division: Division?)

/**
        Converted Network results to database objects
 */

/*
fun NetworkElectionContainer.asDomainModel(): Election {
    return map {
        Election (
        id = it.id,
        name = it.name,
        electionDate = it.electionDate,
        division = it.division)
    }


}

 */
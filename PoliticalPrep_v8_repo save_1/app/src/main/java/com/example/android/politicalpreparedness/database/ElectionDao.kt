package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllElections(election: Election)

    //TO DO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE) //why on this one???
    suspend fun insert(election: Election)

    @Update
    suspend fun update(election: Election)


    //TO DO: Add select all election query
    @Query("SELECT * FROM election_table ORDER BY electionDay DESC")        //this is sql query
    fun getAllElections(): LiveData/*Flow*/<List<Election>>     //this is the method call. use flow instead of LiveData  Flow<List<Election>>

    //Get election when id matches.
   @Query("SELECT * from election_table WHERE id = :key")
    suspend fun getSelectedElection(key: Int): Election?

    //TO DO: Add select single election query
    @Query("SELECT * FROM election_table ORDER BY id DESC LIMIT 1")
    suspend fun getElection(): Election?

    //TO DO: Add delete query
    @Query("DELETE FROM election_table")
    suspend fun clear()

    //TODO: Add clear query
    //@Query("DELETE FROM election_table")
    //suspend fun delete()

}
package com.example.android.politicalpreparedness.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election


//this is the overall binding Adapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter?
    adapter?.submitList(data)
}



//create an extention function of TextView
@BindingAdapter("electionTitle")
fun TextView.setElectionTitle(item: Election?) {
    item?.let{
        text = item.name
    }


@BindingAdapter("electionDate")
fun TextView.setElectionDate(item: Election?) {
    item?.let {
     //   text = item.electionDay.time.toString()         //this needs to be formatted
        text = convertToDateString(item.electionDay, context.resources)

    }
}


}
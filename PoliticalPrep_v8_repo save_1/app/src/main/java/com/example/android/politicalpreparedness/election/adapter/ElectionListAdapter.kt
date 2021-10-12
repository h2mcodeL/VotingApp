package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionItemViewBinding
import com.example.android.politicalpreparedness.network.models.Election

//my version

class ElectionListAdapter (private val clickListener: OnClickListener):
       ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {


    //TO DO: Create ElectionViewHolder -- make the class a private constructor and also use data binding by using the ListAdapter
    class ElectionViewHolder (private val binding: ElectionItemViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election) {      //create a function to bind the data required, pass in parameter named as item of an Election
            binding.electionList = item   //from this we now use binding to the xml views.
            binding.executePendingBindings()
        }

        //TO DO: Add companion object to inflate ViewHolder (from)
        //move companion object into ViewHolderclass
        //we use binding to inflate our layouts instead of the simple R.layout.....
        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ElectionItemViewBinding.inflate(layoutInflater, parent, false)
                return ElectionViewHolder(binding)
            }
        }
    }


    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
       // the old way holder.bind(getItem(position)!!, clickListener)

        val electionProperty = getItem(position)
        holder.itemView.setOnClickListener {        //itemView is a buildin View function
            clickListener.onClick(electionProperty)     //clickListener from parameters above, is assigned
        }
        holder.bind(electionProperty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)

    }

    //TO DO: Create ElectionDiffCallback
    class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            //TO DO("Not yet implemented")
                return oldItem.id == newItem.id
            }


        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            //TO DO("Not yet implemented")
            return oldItem == newItem
        }
    }

    //TO DO: Create ElectionListener
    class OnClickListener(val clickListener: (election : Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }
}


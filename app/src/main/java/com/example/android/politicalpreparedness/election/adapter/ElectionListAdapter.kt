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
        ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    class ElectionViewHolder (private var binding: ElectionItemViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election) {

            binding.electionList = election   //from this we now use binding to the xml views.
           // binding.clickListener = clickListener
            binding.executePendingBindings()
        }
            }

        //move companion object into ViewHolderclass
        //we use binding to inflate our layouts instead of the simple R.layout.....
        companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {

            override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
                return oldItem.id == newItem.id
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionListAdapter.ElectionViewHolder {
        return ElectionViewHolder((ElectionItemViewBinding.inflate(LayoutInflater.from(parent.context))))

    }

    override fun onBindViewHolder(holder: ElectionListAdapter.ElectionViewHolder, position: Int) {
        val electionProperty = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onClick(electionProperty)
        }
        holder.bind(electionProperty)
    }


    //TO DO: Create ElectionListener
    class OnClickListener(val clickListener: (election : Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }
}
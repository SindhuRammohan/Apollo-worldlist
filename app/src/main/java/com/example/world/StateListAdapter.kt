package com.example.world

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.world.databinding.StateItemBinding

class StateListAdapter(private val states: List<StateListQuery.State>) :
    RecyclerView.Adapter<StateListAdapter.ViewHolder>() {

    class ViewHolder(val binding: StateItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return states.size
    }

    var onEndOfListReached: (() -> Unit)? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val state = states[position]
        holder.binding.stateName.text = state.name

        if (position == states.size - 1) {
            onEndOfListReached?.invoke()
        }
    }
}
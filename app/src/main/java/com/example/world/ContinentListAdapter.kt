package com.example.world

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.world.databinding.ContinentItemBinding

class ContinentListAdapter(private val continents: List<ContinentListQuery.Continent>) :
    RecyclerView.Adapter<ContinentListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ContinentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ContinentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return continents.size
    }

    var onEndOfListReached: (() -> Unit)? = null
    var onItemClicked: ((ContinentListQuery.Continent) -> Unit)? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val continent = continents[position]
        holder.binding.continentName.text = continent.name

        if (position == continents.size - 1) {
            onEndOfListReached?.invoke()
        }
        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(continent)
        }
    }
}
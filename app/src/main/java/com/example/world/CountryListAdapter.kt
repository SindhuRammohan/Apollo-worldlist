package com.example.world

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.world.databinding.CountryItemBinding

class CountryListAdapter(private val countries: List<CountryListQuery.Country>) :
    RecyclerView.Adapter<CountryListAdapter.ViewHolder>() {

    class ViewHolder(val binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    var onEndOfListReached: (() -> Unit)? = null
    var onItemClicked: ((CountryListQuery.Country) -> Unit)? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.binding.countryName.text = country.name

        if (position == countries.size - 1) {
            onEndOfListReached?.invoke()
        }
        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(country)
        }
    }
}
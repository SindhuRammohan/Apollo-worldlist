package com.example.world

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.example.world.databinding.CountryDetailsFragmentBinding
import kotlinx.coroutines.channels.Channel

class CountryListFragment : Fragment() {

    private lateinit var binding: CountryDetailsFragmentBinding
    private val args: CountryListFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CountryDetailsFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countries = mutableListOf<CountryListQuery.Country>()
        val adapter = CountryListAdapter(countries)
        binding.countries.layoutManager = LinearLayoutManager(requireContext())
        binding.countries.adapter = adapter

        val channel = Channel<Unit>(Channel.CONFLATED)

        // Send a first item to do the initial load else the list will stay empty forever
        channel.trySend(Unit)
        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        lifecycleScope.launchWhenResumed {
            for (item in channel) {
                val response = try {
                    apolloClient(requireContext()).query(CountryListQuery(id = args.countryId))
                        .execute()

                } catch (e: ApolloException) {
                    Log.d("CountryList", "Failure", e)
                    return@launchWhenResumed
                }

                val newCountries = response.data?.continent?.countries
                binding.countryToolbar.title = response.data?.continent?.name
                if (newCountries != null) {
                    countries.addAll(newCountries)
                    adapter.notifyDataSetChanged()
                } else{
                    Log.e("Country is null and is not added in the list", "Null list")
                }

                if (response.data?.continent?.countries?.isEmpty() != true) {
                    break
                } else{
                    Log.d("Country list is empty and is not added", "Empty list")
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
        adapter.onItemClicked = { launch ->
            findNavController().navigate(
                CountryListFragmentDirections.listState(stateId = launch.code)
            )
        }
    }

}
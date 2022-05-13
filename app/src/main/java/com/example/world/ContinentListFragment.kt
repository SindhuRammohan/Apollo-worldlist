package com.example.world

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.example.world.databinding.ContinentListFragmentBinding
import kotlinx.coroutines.channels.Channel

class ContinentListFragment : Fragment() {
    private lateinit var binding: ContinentListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContinentListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val continents = mutableListOf<ContinentListQuery.Continent>()
        val adapter = ContinentListAdapter(continents)
        binding.continents.layoutManager = LinearLayoutManager(requireContext())
        binding.continents.adapter = adapter

        val channel = Channel<Unit>(Channel.CONFLATED)

        // Send a first item to do the initial load else the list will stay empty forever
        channel.trySend(Unit)
        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        lifecycleScope.launchWhenResumed {
            for (item in channel) {
                val response = try {
                    apolloClient(requireContext()).query(ContinentListQuery())
                        .execute()
                } catch (e: ApolloException) {
                    Log.e("ContinentList", "Failure", e)
                    return@launchWhenResumed
                }

                val newContinents = response.data?.continents

                if (newContinents != null) {
                    continents.addAll(newContinents)
                    adapter.notifyDataSetChanged()
                } else{
                    Log.e("Continent is null and is not added in the list", "Null list")
                }

                if (response.data?.continents?.isEmpty() != true) {
                    break
                } else{
                    Log.d("Continent list is empty and is not added", "Empty list")
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
        adapter.onItemClicked = { launch ->
            findNavController().navigate(
                ContinentListFragmentDirections.openCountryDetails(countryId = launch.code)
            )
        }
    }
}
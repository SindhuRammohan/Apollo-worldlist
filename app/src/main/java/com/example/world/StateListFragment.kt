package com.example.world

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.example.world.databinding.StateDetailsFragmentBinding
import kotlinx.coroutines.channels.Channel

class StateListFragment   : Fragment() {

    private lateinit var binding: StateDetailsFragmentBinding
    val args: StateListFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = StateDetailsFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val states = mutableListOf<StateListQuery.State>()
        val adapter = StateListAdapter(states)
        binding.states.layoutManager = LinearLayoutManager(requireContext())
        binding.states.adapter = adapter



        val channel = Channel<Unit>(Channel.CONFLATED)

        // Send a first item to do the initial load else the list will stay empty forever
        channel.trySend(Unit)
        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        lifecycleScope.launchWhenResumed {
            for (item in channel) {
                val response = try {
                    apolloClient(requireContext()).query(StateListQuery(id = args.stateId)).execute()

                } catch (e: ApolloException) {
                    Log.d("StateList", "Failure", e)
                    return@launchWhenResumed
                }

                val newStates = response.data?.country?.states
                binding.stateToolbar.title = response.data?.country?.name
                if (newStates != null) {
                    states.addAll(newStates)
                    adapter.notifyDataSetChanged()
                }else{
                    Log.e("State is null and is not added in the list", "Null list")
                }

                if (response.data?.country?.states?.isEmpty() != true) {
                    binding.states.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    break
                } else{
                    binding.states.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
    }

}
package org.rionlabs.tatsu.ui.screen.main.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    private val viewModel: StatsViewModel by viewModel()

    private val statsAdapter = StatsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(inflater, container, false).apply {
            statsRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
                adapter = statsAdapter
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timerListData.observe(viewLifecycleOwner, Observer<List<Timer>> {
            val timerList = it ?: return@Observer
            binding.listEmpty = timerList.isEmpty()
            statsAdapter.submitList(timerList)
        })
    }
}
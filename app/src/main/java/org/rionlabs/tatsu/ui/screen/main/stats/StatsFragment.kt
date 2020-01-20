package org.rionlabs.tatsu.ui.screen.main.stats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import kotlinx.android.synthetic.main.activity_main.*
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.databinding.FragmentStatsBinding
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.ui.screen.main.MainViewModel

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private lateinit var viewModel: MainViewModel

    private val statsAdapter = StatsAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
            adapter = statsAdapter
        }

        viewModel.timerListData.observe(viewLifecycleOwner, Observer<List<Timer>> {
            it?.let { timerList ->
                statsAdapter.submitList(timerList)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).fab.setImageResource(R.drawable.ic_navigate_next)
    }
}
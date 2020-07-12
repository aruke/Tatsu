package org.rionlabs.tatsu.ui.screen.main.stats

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.FragmentStatsBinding
import kotlin.math.roundToInt
import kotlin.random.Random

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    private val viewModel: StatsViewModel by viewModel()

    private lateinit var digitsFont: Typeface

    private lateinit var appFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        digitsFont = ResourcesCompat.getFont(requireContext(), R.font.source_code_pro)!!
        appFont = ResourcesCompat.getFont(requireContext(), R.font.raleway)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        initMonthlyChart()
        initYearlyChart()
        // monthlyChart.barData = barData
        // monthlyChart.data = barData
        // monthlyChart.invalidate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timerListData.observe(viewLifecycleOwner, Observer {
            val timerList = it ?: return@Observer
            binding.listEmpty = timerList.isEmpty()
        })
    }

    private fun initMonthlyChart() = with(binding.monthlyChart) {
        val random = Random(100)
        val entries = mutableListOf<BarEntry>()
        for (i in 1..30) {
            entries.add(
                BarEntry(i.toFloat(), random.nextInt(4, 8).toFloat())
            )
        }

        val dataSet = BarDataSet(entries, "").apply {
            this.setDrawValues(false)
            color = ContextCompat.getColor(requireContext(), R.color.color_primary)
            barBorderWidth = 0f
            highLightAlpha = 0
            setDrawIcons(false)
        }
        // Add entries to DataSet
        // val barData = BarData(dataSet).apply {
        //     setDrawValues(false)
        // }


        // XAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1.0f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.roundToInt().toString()
                }
            }
        }

        // Legend
        legend.isEnabled = false

        // Left YAxis
        axisLeft.apply {
            setDrawAxisLine(false)
            typeface = digitsFont
            gridColor = ContextCompat.getColor(requireContext(), R.color.divider)
        }

        // Right YAxis
        axisRight.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        description.isEnabled = false
    }

    private fun initYearlyChart() = with(binding.yearlyChart) {
        val random = Random(100)
        val yearEntries = mutableListOf<Entry>()
        for (i in 1..52) {
            yearEntries.add(
                Entry(i.toFloat(), random.nextInt(21, 42).toFloat())
            )
        }

        val lineDataSet = LineDataSet(yearEntries, "").apply {
            this.setDrawValues(false)
            color = ContextCompat.getColor(requireContext(), R.color.color_primary)
            setDrawIcons(false)
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawHighlightIndicators(false)
        }

        val lineData = LineData(lineDataSet).apply {
            setDrawValues(false)
        }


        // XAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1.0f
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return barEntry?.x?.roundToInt().toString()
                }

                override fun getFormattedValue(value: Float): String {
                    return value.roundToInt().toString()
                }
            }
        }

        // Legend
        legend.isEnabled = false

        // Left Y Axis
        axisLeft.apply {
            setDrawAxisLine(false)
            typeface = digitsFont
            gridColor = ContextCompat.getColor(requireContext(), R.color.divider)
        }

        // Right YAxis
        axisRight.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        description.isEnabled = false

        data = lineData
        invalidate()
    }
}
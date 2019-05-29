package org.rionlabs.tatsu.ui.screen.main.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.databinding.ItemTimerBinding
import org.rionlabs.tatsu.utils.TimeUtils

class StatsAdapter : ListAdapter<Timer, StatsAdapter.TimerViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TimerViewHolder(ItemTimerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TimerViewHolder(private val binding: ItemTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(timer: Timer) {
            binding.timer = timer
            binding.apply {
                timerTypeText.text = timer.type.name
                timerTimeText.text =
                    TimeUtils.toTimeString(root.context, timer.startTime, timer.endTime)
                timerDurationText.text =
                    TimeUtils.toDurationString(root.context, timer.minutes.toInt())
            }
            binding.executePendingBindings()
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Timer>() {
            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem == newItem
            }
        }
    }
}
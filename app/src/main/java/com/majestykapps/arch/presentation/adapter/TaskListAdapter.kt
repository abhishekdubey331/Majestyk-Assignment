package com.majestykapps.arch.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task

class TaskListAdapter(private val onClick: (Task) -> Unit) :
    ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_item, parent, false), onClick
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(itemView: View, private val onClick: (Task) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.title_tv)
        private val taskDescription: TextView = itemView.findViewById(R.id.description_tv)
        private val taskItem: LinearLayout = itemView.findViewById(R.id.taskItem)

        fun bind(task: Task) {
            with(task) {
                taskTitle.text = title
                taskDescription.text = description
                taskItem.setOnClickListener {
                    onClick(this)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem == newItem
        }
    }
}
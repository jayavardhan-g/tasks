package com.example.tasks.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.R
import com.example.tasks.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val onItemClicked: (Task) -> Unit,
    private val onTaskCheckedChange: (Task, Boolean) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        private val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val deadlineView: TextView = itemView.findViewById(R.id.textViewDeadline)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(task: Task) {
            titleView.text = task.title
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            deadlineView.text = "Due: ${dateFormat.format(Date(task.deadline))}"
            
            checkBox.isChecked = task.isCompleted
            
            if (task.isCompleted) {
                titleView.paintFlags = titleView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleView.paintFlags = titleView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onTaskCheckedChange(task, isChecked)
            }
            
            deleteButton.setOnClickListener {
                onDeleteClicked(task)
            }
            
            itemView.setOnClickListener {
                onItemClicked(task)
            }
        }
    }

    class TaskComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}

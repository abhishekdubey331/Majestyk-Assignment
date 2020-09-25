package com.majestykapps.arch.presentation.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task
import com.majestykapps.arch.presentation.adapter.TaskListAdapter
import kotlinx.android.synthetic.main.fragment_tasks.*

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {
            loadingEvent.observe(viewLifecycleOwner, { isRefreshing ->
                Log.d(TAG, "loadingEvent observed")
                swipeRefresh.isRefreshing = isRefreshing
            })

            errorEvent.observe(viewLifecycleOwner, { throwable ->
                Log.e(TAG, "errorEvent observed", throwable)
                // TODO show error
            })

            tasks.observe(viewLifecycleOwner, { tasks ->
                Log.d(TAG, "tasks observed: $tasks")
                setUpRecyclerView(tasks)
            })
        }
    }

    private fun setUpRecyclerView(taskList: List<Task>) {
        val adapter = TaskListAdapter {

        }
        task_list_recycler.adapter = adapter
        task_list_recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(taskList)
    }

    companion object {
        private const val TAG = "TasksFragment"

        fun newInstance() = TasksFragment()
    }
}
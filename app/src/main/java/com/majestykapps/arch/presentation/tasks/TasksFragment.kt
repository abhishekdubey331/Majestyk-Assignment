package com.majestykapps.arch.presentation.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task
import com.majestykapps.arch.presentation.adapter.TaskListAdapter
import com.majestykapps.arch.util.changeVisibility
import kotlinx.android.synthetic.main.fragment_tasks.*
import timber.log.Timber

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by activityViewModels()
    private var taskList = mutableListOf<Task>()

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
                Timber.tag(TAG).d("loadingEvent observed")
                swipeRefresh.isRefreshing = isRefreshing
            })

            errorEvent.observe(viewLifecycleOwner, { throwable ->
                Timber.tag(TAG).d("errorEvent observed $throwable")
                showErrorWithRetry()
            })

            tasks.observe(viewLifecycleOwner, { tasks ->
                Timber.tag(TAG).d("tasks observed: $tasks")
                taskList = tasks.toMutableList()
                setUpRecyclerView(taskList)
            })

            searchQuery.observe(viewLifecycleOwner, { searchQuery ->
                val filteredData = taskList.filter {
                    it.title.contains(searchQuery) || it.description.contains(searchQuery)
                }
                setUpRecyclerView(filteredData)
            })
        }
    }

    private fun showErrorWithRetry() {
        val snackBar = Snackbar
            .make(parentView, getString(R.string.something_went_wrong), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                viewModel.refresh()
            }
        snackBar.show()
    }

    override fun onResume() {
        super.onResume()
        setUpRecyclerView(taskList)
    }

    private fun setUpRecyclerView(adapterData: List<Task>) {
        val adapter = TaskListAdapter {
            viewModel.launchEvent.value = it.id
        }
        task_list_recycler.adapter = adapter
        task_list_recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(adapterData)
        empty_task_view.changeVisibility(adapterData.isEmpty())
    }

    companion object {
        private const val TAG = "TasksFragment"

        fun newInstance() = TasksFragment()
    }
}
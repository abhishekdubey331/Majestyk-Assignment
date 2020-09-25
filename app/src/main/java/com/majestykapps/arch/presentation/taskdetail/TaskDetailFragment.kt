package com.majestykapps.arch.presentation.taskdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.majestykapps.arch.R
import com.majestykapps.arch.data.repository.TasksRepositoryImpl
import com.majestykapps.arch.data.source.local.TasksLocalDataSource
import com.majestykapps.arch.data.source.local.ToDoDatabase
import com.majestykapps.arch.presentation.common.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_task_detail.*

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    private lateinit var viewModel: TaskDetailViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = initViewModel()
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {

            getTask(arguments?.getString("ID")!!)

            loadingEvent.observe(viewLifecycleOwner, { isRefreshing ->

            })

            errorEvent.observe(viewLifecycleOwner, { throwable ->
                // TODO show error
            })

            title.observe(viewLifecycleOwner, { taskTitle ->
                title_tv.text = taskTitle
            })

            description.observe(viewLifecycleOwner, { taskDescription ->
                description_tv.text = taskDescription
            })
        }
    }

    private fun initViewModel(): TaskDetailViewModel {
        val tasksDao = ToDoDatabase.getInstance(this.requireActivity().applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TaskDetailViewModel::class.java)
    }
}
package com.majestykapps.arch.presentation.taskdetail

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.majestykapps.arch.R
import com.majestykapps.arch.data.repository.TasksRepositoryImpl
import com.majestykapps.arch.data.source.local.TasksLocalDataSource
import com.majestykapps.arch.data.source.local.ToDoDatabase
import com.majestykapps.arch.presentation.common.ViewModelFactory
import com.majestykapps.arch.util.changeVisibility
import kotlinx.android.synthetic.main.fragment_task_detail.*

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    private lateinit var viewModel: TaskDetailViewModel

    companion object {
        const val TASK_ID = "TASK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = initViewModel()
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {
            arguments?.getString(TASK_ID)?.let {
                getTask(it)
            }
            errorEvent.observe(viewLifecycleOwner, {
                empty_task_view.changeVisibility(true)
            })

            title.observe(viewLifecycleOwner, { taskTitle ->
                title_tv.text = taskTitle
            })

            description.observe(viewLifecycleOwner, { taskDescription ->
                description_tv.text = taskDescription
            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    private fun initViewModel(): TaskDetailViewModel {
        val tasksDao = ToDoDatabase.getInstance(this.requireActivity().applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TaskDetailViewModel::class.java)
    }
}

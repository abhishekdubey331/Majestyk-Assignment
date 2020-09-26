package com.majestykapps.arch

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.majestykapps.arch.data.repository.TasksRepositoryImpl
import com.majestykapps.arch.data.source.local.TasksLocalDataSource
import com.majestykapps.arch.data.source.local.ToDoDatabase
import com.majestykapps.arch.presentation.common.ViewModelFactory
import com.majestykapps.arch.presentation.taskdetail.TaskDetailFragment
import com.majestykapps.arch.presentation.tasks.TasksFragment
import com.majestykapps.arch.presentation.tasks.TasksViewModel
import com.majestykapps.arch.util.RxSearchObservable
import com.majestykapps.arch.util.getTaskId
import com.majestykapps.arch.util.newFragmentInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var tasksViewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tasksViewModel = initViewModel()
        initViewModelObservers()

        if (intent?.data != null) {
            val data = intent.data
            val taskId = URL(data.toString()).getTaskId()
            navigateToTaskDetail(taskId)
        } else if (savedInstanceState == null) {
            fragmentNavigation(TasksFragment.newInstance())
        }
    }

    private fun navigateToTaskDetail(taskId: String) {
        fragmentNavigation(newFragmentInstance<TaskDetailFragment>(TaskDetailFragment.TASK_ID to taskId))
    }

    private fun fragmentNavigation(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.mainContent, fragment)
            .commit()
    }

    private fun initViewModel(): TasksViewModel {
        val tasksDao = ToDoDatabase.getInstance(applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TasksViewModel::class.java)
    }

    private fun initViewModelObservers() {
        tasksViewModel.apply {
            launchEvent.observe(this@MainActivity, { id ->
                Log.d(TAG, "launchTask: launching task with id = $id")
                navigateToTaskDetail(id)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.action_search)
        val searchView = search.actionView as SearchView?
        searchView?.apply {
            this.queryHint = getString(R.string.search)
            handleSearch(this)
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun handleSearch(searchView: SearchView) {
        tasksViewModel.disposables.add(
            RxSearchObservable.fromView(searchView)
                .debounce(SEARCH_MIN_TIMEOUT, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    tasksViewModel.searchQueryMutableLiveData.value = result
                }
        )
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount < MIN_FRAGMENT_BACK_STACK_COUNT)
            this.finish()
        else
            supportFragmentManager.popBackStackImmediate()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val MIN_FRAGMENT_BACK_STACK_COUNT = 2
        private const val SEARCH_MIN_TIMEOUT = 300L
    }
}

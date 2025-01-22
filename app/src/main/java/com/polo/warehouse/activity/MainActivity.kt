package com.polo.warehouse.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import com.polo.warehouse.R
import com.polo.core.R as coreR
import com.polo.warehouse.R.layout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        setUpStartingDestination()
    }

    private fun setUpStartingDestination() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostContainer) as NavHostFragment

        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(coreR.navigation.nav_graph)
        navGraph.setStartDestination(viewModel.getStartingDestination())
        navController.graph = navGraph
    }
}
package com.polo.warehouse.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.polo.warehouse.R
import com.polo.core.R as coreR
import com.polo.warehouse.R.layout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        navController = (supportFragmentManager.findFragmentById(R.id.navHostContainer) as NavHostFragment).navController

        setUpStartingDestination()
    }

    private fun setUpStartingDestination() {

        navController.apply {

            val navGraph = navInflater.inflate(coreR.navigation.nav_graph)
            val destination = viewModel.getStartingDestination()
            navGraph.setStartDestination(destination)
            graph = navGraph
        }
    }
}
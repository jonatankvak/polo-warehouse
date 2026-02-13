package com.polo.warehouse.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.polo.designsystem.theme.AppTheme
import com.polo.warehouse.navigation.PoloWarehouseNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = viewModel.getStartingDestination()

        setContent {
            AppTheme {
                PoloWarehouseNavHost(startDestination = startDestination)
            }
        }
    }
}

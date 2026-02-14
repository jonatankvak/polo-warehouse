package com.polo.warehouse.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.polo.designsystem.theme.AppTheme
import com.polo.warehouse.navigation.PoloWarehouseNavHost
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

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

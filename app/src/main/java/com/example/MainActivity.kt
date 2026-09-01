package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.biomax.ui.BiomaxApp
import com.example.biomax.viewmodel.BiomaxViewModel
import com.example.ui.theme.BiomaxTheme

class MainActivity : ComponentActivity() {
    private val biomaxViewModel: BiomaxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appSettings by biomaxViewModel.appSettings.collectAsStateWithLifecycle()

            BiomaxTheme(
                themeMode = appSettings.themeMode,
                themePalette = appSettings.themePalette,
                dynamicColor = appSettings.dynamicColor
            ) {
                BiomaxApp(viewModel = biomaxViewModel)
            }
        }
    }
}

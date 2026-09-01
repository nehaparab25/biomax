package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.biomax.ui.BiomaxApp
import com.example.biomax.viewmodel.BiomaxViewModel
import com.example.ui.theme.BiomaxTheme

class MainActivity : ComponentActivity() {
    private val biomaxViewModel: BiomaxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BiomaxTheme {
                BiomaxApp(viewModel = biomaxViewModel)
            }
        }
    }
}

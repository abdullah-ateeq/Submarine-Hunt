package com.example.submarinehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.submarinehunt.ui.navigation.MainNavigation
import com.example.submarinehunt.ui.theme.SubmarineHuntTheme
import com.example.submarinehunt.ui.viewmodel.SubmarineHuntViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SubmarineHuntViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            SubmarineHuntTheme(
                themeSetting = settings.theme,
                dynamicColor = settings.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
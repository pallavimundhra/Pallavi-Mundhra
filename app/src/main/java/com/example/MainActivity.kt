package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.SkinDatabase
import com.example.data.SkinAnalysisRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.AuraSkinTheme
import com.example.ui.viewmodel.AuraSkinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Initialize Room Local Database & Repository
        val database = SkinDatabase.getDatabase(applicationContext)
        val repository = SkinAnalysisRepository(database.dao())
        
        // 2. Instantiate AuraSkin ViewModel
        val viewModel = ViewModelProvider(
            this,
            AuraSkinViewModel.Factory(application, repository)
        )[AuraSkinViewModel::class.java]
        
        // 3. Enable Fullscreen Edge-To-Edge drawing safe-zones
        enableEdgeToEdge()
        
        // 4. Mount User Interface
        setContent {
            AuraSkinTheme {
                MainScreen(viewModel)
            }
        }
    }
}

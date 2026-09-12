package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather.ui.screens.ForecastScreen
import com.example.weather.ui.screens.MainScreen
import com.example.weather.ui.theme.WeatherAppTheme
import com.example.weather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp()
                }
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = viewModel() // Один ViewModel на все приложение

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToForecast = { cityName ->
                    // Передаем город как параметр в URL
                    navController.navigate("forecast/$cityName")
                }
            )
        }
        composable("forecast/{cityName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            ForecastScreen(
                viewModel = viewModel,
                cityName = cityName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
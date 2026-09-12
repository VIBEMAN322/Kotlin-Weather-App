package com.example.weather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    viewModel: WeatherViewModel, // УБРАЛИ значение по умолчанию!
    cityName: String, // ДОБАВИЛИ параметр cityName
    onNavigateBack: () -> Unit
) {
    val forecast by viewModel.forecast.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val currentCityName by viewModel.cityName.collectAsState()

    // Используем переданный cityName или из ViewModel
    val displayCityName = cityName.ifBlank { currentCityName }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Прогноз на 3 дня") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (forecast != null && forecast!!.list.isNotEmpty()) {
                // Заголовок
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayCityName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Прогноз погоды на 3 дня",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Группируем прогноз по дням
                val dailyForecasts = groupForecastByDay(forecast!!.list)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(dailyForecasts) { dayForecast ->
                        DailyForecastItem(
                            date = dayForecast.first,
                            forecasts = dayForecast.second,
                            temperatureUnit = temperatureUnit
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (displayCityName.isNotBlank()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Город: $displayCityName",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Загрузка прогноза...")

                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = onNavigateBack) {
                                    Text("Вернуться и загрузить")
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Введите город на главном экране",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = onNavigateBack) {
                                    Text("Вернуться")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    date: String,
    forecasts: List<com.example.weather.model.ForecastItem>,
    temperatureUnit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            forecasts.forEach { forecast ->
                HourlyForecastRow(forecast, temperatureUnit)
                if (forecast != forecasts.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun HourlyForecastRow(
    forecast: com.example.weather.model.ForecastItem,
    temperatureUnit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(forecast.dt * 1000)),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = getWeatherEmoji(forecast.weather.firstOrNull()?.main ?: ""),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = "${forecast.main.temp.toInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Text(
            text = forecast.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f),
            maxLines = 1
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "💧${forecast.main.humidity}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun groupForecastByDay(forecastList: List<com.example.weather.model.ForecastItem>): List<Pair<String, List<com.example.weather.model.ForecastItem>>> {
    val grouped = mutableMapOf<String, MutableList<com.example.weather.model.ForecastItem>>()
    val dateFormat = SimpleDateFormat("E, dd MMMM", Locale.getDefault())

    forecastList.forEach { forecast ->
        val date = dateFormat.format(Date(forecast.dt * 1000))
        grouped.getOrPut(date) { mutableListOf() }.add(forecast)
    }

    // Берем только первые 3 дня
    return grouped.entries.take(3).map { it.key to it.value }
}

private fun getWeatherEmoji(weatherCondition: String): String {
    return when (weatherCondition.lowercase()) {
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "rain" -> "🌧️"
        "snow" -> "❄️"
        "thunderstorm" -> "⛈️"
        "drizzle" -> "🌦️"
        else -> "☀️"
    }
}
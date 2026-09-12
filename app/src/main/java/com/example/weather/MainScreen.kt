package com.example.weather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.R
import com.example.weather.ui.components.WeatherCard
import com.example.weather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    viewModel: WeatherViewModel, // УБРАЛИ значение по умолчанию!
    onNavigateToForecast: (String) -> Unit // ИЗМЕНИЛИ сигнатуру - теперь принимает String
) {
    val context = LocalContext.current
    val currentWeather by viewModel.currentWeather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val cityName by viewModel.cityName.collectAsState() // Используем это значение
    val showForecast by viewModel.showForecast.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()

    var cityInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Погодное приложение",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Поле ввода города
        OutlinedTextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            label = { Text("Введите город") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.fetchWeather(cityInput)
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (cityInput.isNotBlank()) {
                    IconButton(onClick = { cityInput = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка поиска
        Button(
            onClick = {
                viewModel.fetchWeather(cityInput)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = cityInput.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Загрузка...")
            } else {
                Text("Получить погоду")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Переключатель единиц измерения
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Единицы измерения:")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = temperatureUnit == "°F",
                onCheckedChange = { viewModel.toggleTemperatureUnit() }
            )
            Text(if (temperatureUnit == "°F") "°F" else "°C")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Чекбокс для показа прогноза
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = showForecast,
                onCheckedChange = { viewModel.toggleForecastVisibility() }
            )
            Text("Показать прогноз на сегодня")
        }

        // Показать ошибку
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Отображение погоды
        currentWeather?.let { weather ->
            Spacer(modifier = Modifier.height(24.dp))
            WeatherCard(
                weather = weather,
                temperatureUnit = temperatureUnit,
                showForecast = showForecast,
                forecastResponse = forecast,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка перехода к детальному прогнозу
            // Теперь передаем cityName при навигации
            if (cityName.isNotBlank()) {
                Button(
                    onClick = {
                        // Передаем имя города в функцию навигации
                        onNavigateToForecast(cityName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cityName.isNotBlank()
                ) {
                    Text("Подробный прогноз на 3 дня")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        } ?: run {
            if (!isLoading && errorMessage == null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Введите город для отображения погоды",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Примеры городов
        if (currentWeather == null && !isLoading) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Примеры городов:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Москва", "Лондон", "Нью-Йорк", "Токио").forEach { city ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            cityInput = city
                            viewModel.fetchWeather(city)
                        },
                        label = { Text(city) }
                    )
                }
            }
        }
    }
}
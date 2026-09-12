package com.example.weather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.weather.model.ForecastResponse
import com.example.weather.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherCard(
    weather: WeatherResponse,
    temperatureUnit: String,
    showForecast: Boolean,
    forecastResponse: ForecastResponse?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок с городом
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "${weather.name}, ${weather.sys.country}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                            .format(Date(weather.dt * 1000)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Иконка погоды
                Icon(
                    imageVector = getWeatherIcon(weather.weather.firstOrNull()?.main ?: ""),
                    contentDescription = weather.weather.firstOrNull()?.description,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Основная информация
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "${formatTemperature(weather.main.temp, temperatureUnit)} $temperatureUnit",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = "Ощущается как ${formatTemperature(weather.main.feelsLike, temperatureUnit)} $temperatureUnit",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = weather.weather.firstOrNull()?.description?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        } ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Влажность: ${weather.main.humidity}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Дополнительная информация
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                WeatherInfoItem(
                    icon = Icons.Filled.Air,
                    title = "Ветер",
                    value = "${weather.wind.speed} м/с"
                )

                WeatherInfoItem(
                    icon = Icons.Filled.WaterDrop,
                    title = "Давление",
                    value = "${weather.main.pressure} гПа"
                )

                WeatherInfoItem(
                    icon = Icons.Filled.WbSunny,
                    title = "Восход",
                    value = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(weather.sys.sunrise * 1000))
                )

                WeatherInfoItem(
                    icon = Icons.Filled.NightsStay,
                    title = "Закат",
                    value = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(weather.sys.sunset * 1000))
                )
            }

            // Прогноз на сегодня (если включен)
            if (showForecast && forecastResponse != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Прогноз на сегодня:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Берем первые 8 записей (24 часа) из forecastResponse.list
                val todayForecast = forecastResponse.list.take(8)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todayForecast) { item ->
                        HourlyForecastItem(forecastItem = item, temperatureUnit = temperatureUnit)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun HourlyForecastItem(
    forecastItem: com.example.weather.model.ForecastItem,
    temperatureUnit: String
) {
    Card(
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(forecastItem.dt * 1000)),
                style = MaterialTheme.typography.labelMedium
            )

            Icon(
                imageVector = getWeatherIcon(forecastItem.weather.firstOrNull()?.main ?: ""),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${formatTemperature(forecastItem.main.temp, temperatureUnit)}°",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = forecastItem.weather.firstOrNull()?.description?.take(5) ?: "",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

private fun getWeatherIcon(weatherCondition: String): ImageVector {
    return when (weatherCondition.lowercase()) {
        "clear" -> Icons.Filled.WbSunny
        "clouds" -> Icons.Filled.Cloud
        "rain" -> Icons.Filled.Grain
        "snow" -> Icons.Filled.AcUnit
        "thunderstorm" -> Icons.Filled.FlashOn
        "drizzle" -> Icons.Filled.Grain
        else -> Icons.Filled.WbSunny
    }
}

private fun formatTemperature(temp: Double, unit: String): String {
    return if (unit == "°F") {
        String.format("%.1f", temp * 9/5 + 32)
    } else {
        String.format("%.1f", temp)
    }
}
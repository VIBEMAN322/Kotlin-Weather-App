package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.ForecastResponse
import com.example.weather.model.WeatherResponse
import com.example.weather.network.WeatherApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    // ВАШ КЛЮЧ OpenWeatherMap API
    private val apiKey = "" // ЗАМЕНИТЕ ЭТО!

    // Создаем сервис с ключом
    private val weatherService = WeatherApiService.create(apiKey)

    private val _currentWeather = MutableStateFlow<WeatherResponse?>(null)
    val currentWeather: StateFlow<WeatherResponse?> = _currentWeather.asStateFlow()

    private val _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast: StateFlow<ForecastResponse?> = _forecast.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _temperatureUnit = MutableStateFlow("°C")
    val temperatureUnit: StateFlow<String> = _temperatureUnit.asStateFlow()

    private val _cityName = MutableStateFlow("")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

    private val _showForecast = MutableStateFlow(false)
    val showForecast: StateFlow<Boolean> = _showForecast.asStateFlow()

    fun fetchWeather(city: String) {
        if (city.isBlank()) {
            _errorMessage.value = "Введите название города"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _cityName.value = city

            try {
                // Теперь вызываем методы БЕЗ передачи apiKey
                val weatherResponse = weatherService.getCurrentWeather(city)
                _currentWeather.value = weatherResponse

                val forecastResponse = weatherService.getForecast(city)
                _forecast.value = forecastResponse
                // Логируем для отладки
                println("Прогноз загружен: ${forecastResponse.list.size} записей")
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message ?: "Неизвестная ошибка"}"
                _currentWeather.value = null
                _forecast.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleTemperatureUnit() {
        _temperatureUnit.value = if (_temperatureUnit.value == "°C") "°F" else "°C"
    }

    fun toggleForecastVisibility() {
        _showForecast.value = !_showForecast.value
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
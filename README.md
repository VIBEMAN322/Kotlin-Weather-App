## Kotlin Weather App

Приложение прогноза погоды для Android, написанное на Kotlin с использованием Jetpack Compose

# Функциональность

-  Поиск текущей погоды по названию города
-  Детальный прогноз погоды
-  Переключение единиц измерения температуры (°C / °F)
-  Обработка состояний загрузки и ошибок
-  Навигация между экранами (главный экран → прогноз)

-  ## Технологии

**Язык и UI:**
- Kotlin
- Jetpack Compose
- Material 3

**Архитектура:**
- MVVM (ViewModel + StateFlow)
- Navigation Compose

**Сеть:**
- Retrofit
- OkHttp
- Gson

**API:**
- [OpenWeatherMap](https://openweathermap.org/api)

# Требования

- Android 7.0 (API 24) и выше
- Android Studio (для сборки и запуска)

# Установка и запуск

1. Склонируйте репозиторий:
```bash
   git clone https://github.com/[ваш_username]/[название_репозитория].git
```

2. Получите бесплатный API-ключ на [OpenWeatherMap](https://openweathermap.org/api) (регистрация занимает пару минут).

3. В корне проекта создайте файл `local.properties` (если его нет) и добавьте туда:
```properties
   WEATHER_API_KEY=ваш_ключ_здесь
```

4. Откройте проект в Android Studio.

5. Дождитесь синхронизации Gradle (может занять несколько минут при первом запуске).

6. Запустите приложение на эмуляторе или подключённом Android-устройстве (кнопка **Run ▶**).

# Автор

VIBEMAN322 — [https://github.com/VIBEMAN322]

# Лицензия

Проект распространяется под лицензией MIT — см. файл [LICENSE](LICENSE).

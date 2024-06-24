data class WeatherData(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val icon: String,
    val description: String
)

data class Wind(
    val speed: Double
)

data class WeatherResponse(
    val name: String,
    val main: MainResponse,
    val weather: List<WeatherResponseItem>,
    val wind: WindResponse
)

data class MainResponse(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class WeatherResponseItem(
    val icon: String,
    val description: String
)

data class WindResponse(
    val speed: Double
)
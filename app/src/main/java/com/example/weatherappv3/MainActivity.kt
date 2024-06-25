package com.example.weatherappv3

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.weatherappv3.ui.theme.WeatherAppFinTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppFinTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(weatherService, "931ba88b5cecc67ee0258cda163ecc60")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(weatherService: WeatherService, apiKey: String) {
        var showIntro by remember { mutableStateOf(true) }
        var selectedItem by remember { mutableStateOf(0) }
        var city by remember { mutableStateOf("Kakanj") }
        var temperature by remember { mutableStateOf("Fetching...") }
        var weatherIconUrl by remember { mutableStateOf("") }

        val items = listOf("Intro", "Home", "Favorites")
        val icons = listOf(Icons.Default.Info, Icons.Default.Home, Icons.Default.Favorite)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Weatherly") },
                    actions = {
                        val context = LocalContext.current
                        if (selectedItem == 1) {
                            IconButton(onClick = { shareWeatherInfo(context, city, temperature) }) {
                                Icon(Icons.Filled.Share, contentDescription = "Share")
                            }
                            IconButton(onClick = { addCityToFavorites(context, city, temperature, weatherIconUrl) }) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Add to Favorites")
                            }
                        } else if (selectedItem == 2) {
                            IconButton(onClick = { shareFavorites(context) }) {
                                Icon(Icons.Filled.Share, contentDescription = "Share Favorites")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = null) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (selectedItem) {
                    0 -> IntroScreen { selectedItem = 1 }
                    1 -> HomeScreen(weatherService, apiKey, city, temperature, weatherIconUrl, onCityChange = { city = it }, onTemperatureChange = { temperature = it }, onIconUrlChange = { weatherIconUrl = it })
                    2 -> FavoritesScreen(weatherService, apiKey)
                }
            }
        }
    }

    private fun shareFavorites(context: Context) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favorites = sharedPreferences.getStringSet("favorites_list", mutableSetOf())?.toList() ?: listOf()
        val favoriteDetails = favorites.map { city ->
            val data = sharedPreferences.getString(city, null)?.split(" ")
            val temperature = data?.get(0) ?: "N/A"
            "$city: $temperature"
        }.joinToString(separator = "\n")

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Favorite Cities:\n$favoriteDetails")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }



    @Composable
    fun IntroScreen(onContinueClicked: () -> Unit) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logow),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Weatherly",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Get accurate and up-to-date weather information for your favorite cities.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onContinueClicked) {
                        Text("Continue")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logow),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to Weatherly",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get accurate and up-to-date weather information for your favorite cities.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onContinueClicked) {
                    Text("Continue")
                }
            }
        }
    }

    @Composable
    fun HomeScreen(
        weatherService: WeatherService,
        apiKey: String,
        city: String,
        temperature: String,
        weatherIconUrl: String,
        onCityChange: (String) -> Unit,
        onTemperatureChange: (String) -> Unit,
        onIconUrlChange: (String) -> Unit
    ) {
        var cityState by remember { mutableStateOf(city) }
        var temperatureState by remember { mutableStateOf(temperature) }
        var humidity by remember { mutableStateOf("") }
        var pressure by remember { mutableStateOf("") }
        var windSpeed by remember { mutableStateOf("") }
        var weatherIconUrlState by remember { mutableStateOf(weatherIconUrl) }
        val coroutineScope = rememberCoroutineScope()
        var tempCity by remember { mutableStateOf(city) }

        LaunchedEffect(cityState) {
            coroutineScope.launch {
                try {
                    val response = weatherService.getWeather(cityState, apiKey)
                    val tempInCelsius = (response.main.temp - 273.15).roundToInt()
                    temperatureState = "$tempInCelsius°C"
                    humidity = "${response.main.humidity}%"
                    pressure = "${response.main.pressure} hPa"
                    windSpeed = "${response.wind.speed} m/s"
                    weatherIconUrlState = "https://openweathermap.org/img/w/${response.weather[0].icon}.png"
                } catch (e: Exception) {
                    temperatureState = "Failed to fetch data"
                    humidity = "Failed to fetch data"
                    pressure = "Failed to fetch data"
                    windSpeed = "Failed to fetch data"
                }
                onCityChange(cityState)
                onTemperatureChange(temperatureState)
                onIconUrlChange(weatherIconUrlState)
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                TextField(
                    value = tempCity,
                    onValueChange = { tempCity = it },
                    label = { Text("Enter city name") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { cityState = tempCity }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = cityState, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = temperatureState, style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (weatherIconUrlState.isNotEmpty()) {
                            GlideImage(url = weatherIconUrlState)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Humidity: $humidity", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Pressure: $pressure", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Wind Speed: $windSpeed", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = cityState, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = temperatureState, style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (weatherIconUrlState.isNotEmpty()) {
                        GlideImage(url = weatherIconUrlState)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Humidity: $humidity", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Pressure: $pressure", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Wind Speed: $windSpeed", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }


    @Composable
    fun FavoritesScreen(weatherService: WeatherService, apiKey: String) {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favorites = sharedPreferences.getStringSet("favorites_list", mutableSetOf())?.toList() ?: listOf()
        val coroutineScope = rememberCoroutineScope()

        var updatedFavorites by remember { mutableStateOf(listOf<Pair<String, WeatherData>>()) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val updatedList = favorites.mapNotNull { city ->
                    try {
                        val response = weatherService.getWeather(city, apiKey)
                        val tempInCelsius = (response.main.temp - 273.15).roundToInt()
                        val weatherData = WeatherData(
                            name = city,
                            temperature = "$tempInCelsius°C",
                            iconUrl = "https://openweathermap.org/img/w/${response.weather[0].icon}.png"
                        )
                        city to weatherData
                    } catch (e: Exception) {
                        null
                    }
                }
                updatedFavorites = updatedList
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(updatedFavorites) { (city, weatherData) ->
                        FavoriteItem(city, weatherData.temperature, weatherData.iconUrl, onDelete = {
                            removeCityFromFavorites(context, city)
                            updatedFavorites = updatedFavorites.filterNot { it.first == city }
                        })
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(updatedFavorites) { (city, weatherData) ->
                        FavoriteItem(city, weatherData.temperature, weatherData.iconUrl, onDelete = {
                            removeCityFromFavorites(context, city)
                            updatedFavorites = updatedFavorites.filterNot { it.first == city }
                        })
                    }
                }
            }
        }
    }

    @Composable
    fun FavoriteItem(city: String, temperature: String, iconUrl: String, onDelete: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(city, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(temperature, style = MaterialTheme.typography.bodyLarge)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (iconUrl.isNotEmpty()) {
                    GlideImage(url = iconUrl)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    @Composable
    fun GlideImage(url: String) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            },
            update = { imageView ->
                Glide.with(imageView.context)
                    .load(url)
                    .into(imageView)
            },
            modifier = Modifier.size(48.dp)
        )
    }

    private fun addCityToFavorites(context: Context, city: String, temperature: String, iconUrl: String) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val favorites = sharedPreferences.getStringSet("favorites_list", mutableSetOf())?.toMutableSet()
        favorites?.add(city)
        editor.putStringSet("favorites_list", favorites)
        editor.putString(city, "$temperature $iconUrl")
        editor.apply()
        Toast.makeText(context, "$city added to favorites", Toast.LENGTH_SHORT).show()
    }

    private fun removeCityFromFavorites(context: Context, city: String) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val favorites = sharedPreferences.getStringSet("favorites_list", mutableSetOf())?.toMutableSet()
        favorites?.remove(city)
        editor.putStringSet("favorites_list", favorites)
        editor.remove(city)
        editor.apply()
        Toast.makeText(context, "$city removed from favorites", Toast.LENGTH_SHORT).show()
    }

    private fun shareWeatherInfo(context: Context, city: String, temperature: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Weather in $city: $temperature")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}

data class WeatherData(
    val name: String,
    val temperature: String,
    val iconUrl: String
)

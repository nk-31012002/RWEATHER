package com.example.rweather.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.rweather.ApiInterface
import com.example.rweather.DataClass.RWeatherApp
import com.example.rweather.R
import com.example.rweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//5c2b66013088cdf4be36dcf553ca2bae

//API Response
//{
//    "coord": {
//    "lon": 21.9167,
//    "lat": 47.15
//},
//    "weather": [
//    {
//        "id": 804,
//        "main": "Clouds",
//        "description": "overcast clouds",
//        "icon": "04n"
//    }
//    ],
//    "base": "stations",
//    "main": {
//    "temp": 280.33,
//    "feels_like": 278.58,
//    "temp_min": 280.02,
//    "temp_max": 280.43,
//    "pressure": 1012,
//    "humidity": 87
//},
//    "visibility": 10000,
//    "wind": {
//    "speed": 2.57,
//    "deg": 140
//},
//    "clouds": {
//    "all": 100
//},
//    "dt": 1702410731,
//    "sys": {
//    "type": 2,
//    "id": 50396,
//    "country": "RO",
//    "sunrise": 1702361352,
//    "sunset": 1702392161
//},
//    "timezone": 7200,
//    "id": 684882,
//    "name": "Biharia",
//    "cod": 200
//}
class MainActivity : AppCompatActivity() {

    private  val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        searchCity()
        fetchWeatherData("Bihar")   // from the Api response
    }

    private fun searchCity() {
        val searchView=binding.citySearchbar
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {

        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName, appid = "5c2b66013088cdf4be36dcf553ca2bae", units = "metric")
        response.enqueue(object : Callback<RWeatherApp>{
            override fun onResponse(call: Call<RWeatherApp>, response: Response<RWeatherApp>) {

                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null)
                {
                    val temperature=responseBody.main.temp.toString()
                    val seaLevel=responseBody.main.pressure
                    val minTemp=responseBody.main.temp_min
                    val maxTemp=responseBody.main.temp_max
                    val humidity=responseBody.main.humidity
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()


//                    set the all the texts
                    binding.dateTv.text=date()
                    binding.celsiusDis.text="$temperature ℃"
                    binding.weatherTv.text=condition
                    binding.maxTv.text="Max Temp: $maxTemp ℃"
                    binding.minTv.text="Min Temp: $minTemp ℃"
                    binding.humidityTv.text="$humidity %"
                    binding.windTv.text="$windSpeed m/s"
                    binding.sunriseTv.text="${time(sunRise)}"
                    binding.sunsetTv.text="${time(sunSet)}"
                    binding.sealevelTv.text="$seaLevel hPa"
                    binding.conditionTv.text=condition
                    binding.dayTv.text= dayName(System.currentTimeMillis())
                        binding.locationTv.text="$cityName"

                    Changebackground(condition)




//                    Log.d("TAG","onResponse:$temperature")

                }
            }

            override fun onFailure(call: Call<RWeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun Changebackground(Conditions: String) {
        when(Conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animatedV.setAnimation(R.raw.sun)
            }

            "Partly Clouds","Clouds","OverCast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animatedV.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animatedV.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animatedV.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animatedV.setAnimation(R.raw.sun)
            }


        }

        binding.animatedV.playAnimation()
    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}
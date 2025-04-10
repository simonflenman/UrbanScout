package se.umu.sifl0010.googlemapsapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import se.umu.sifl0010.googlemapsapplication.adapter.PlacesListAdapter
import se.umu.sifl0010.googlemapsapplication.model.Place
import se.umu.googlemapsapplication.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.app.ActivityCompat

class PlacesListActivity : AppCompatActivity(), PlacesListAdapter.OnItemClickListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacesListAdapter
    private lateinit var tvPlaceholder: TextView
    private lateinit var fabBack: FloatingActionButton
    private val placesList = mutableListOf<Place>()

    // User location and query parameters.
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0
    private var searchRadius: Int = 10000
    private var queryType: String = "restaurant"

    // API key.
    private val API_KEY: String
        get() = getString(R.string.google_maps_key)

    // FusedLocationProviderClient in case location extras are missing.
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)

        // Set up the Toolbar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup DrawerLayout.
        drawerLayout = findViewById(R.id.drawer_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Places List"

        tvPlaceholder = findViewById(R.id.tvPlaceholder)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlacesListAdapter(placesList, this)
        recyclerView.adapter = adapter

        // Setup FloatingActionButton for back action.
        fabBack = findViewById(R.id.fabBack)
        fabBack.setOnClickListener { finish() }

        // Retrieve parameters from intent.
        val selectedCategories = intent.getStringArrayExtra("selectedCategories")?.toSet() ?: emptySet()
        val selectedSubcategories = intent.getStringArrayExtra("selectedSubcategories")?.toSet() ?: emptySet()
        searchRadius = intent.getIntExtra("searchRadius", 10000)
        userLat = intent.getDoubleExtra("userLat", 0.0)
        userLng = intent.getDoubleExtra("userLng", 0.0)

        // Initialize fusedLocationClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // If location extras are not provided, fetch the last known location.
        if (userLat == 0.0 && userLng == 0.0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLat = it.latitude
                    userLng = it.longitude
                    queryNearbyPlaces()
                } ?: run {
                    Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            queryNearbyPlaces()
        }

        if (selectedCategories.isEmpty() && selectedSubcategories.isEmpty()) {
            tvPlaceholder.visibility = View.VISIBLE
            queryType = "restaurant"
        } else {
            tvPlaceholder.visibility = View.GONE
            queryType = when {
                selectedSubcategories.contains("Sports Bars") ||
                        selectedSubcategories.contains("Bars") ||
                        selectedCategories.contains("Drinks") -> "bar"
                selectedSubcategories.contains("Restaurants") ||
                        selectedCategories.contains("Food") -> "restaurant"
                else -> "restaurant"
            }
        }

        // Setup sidebar buttons.
        val btnSearch = drawerLayout.findViewById<Button>(R.id.btnSearch)
        val btnClearFilters = drawerLayout.findViewById<Button>(R.id.btnClearFilters)
        val btnBackDrawer = drawerLayout.findViewById<Button>(R.id.btnBackDrawer)
        btnSearch.setOnClickListener {
            queryNearbyPlaces()
            drawerLayout.closeDrawers()
        }
        btnClearFilters.setOnClickListener {
            queryType = "restaurant"
            Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show()
            queryNearbyPlaces()
            drawerLayout.closeDrawers()
        }
        btnBackDrawer.setOnClickListener {
            drawerLayout.closeDrawers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_drawer -> {
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Queries the Google Places Nearby Search API asynchronously.
     */
    private fun queryNearbyPlaces() {
        if (userLat == 0.0 && userLng == 0.0) {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show()
            return
        }
        val locationStr = "$userLat,$userLng"
        val urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationStr&radius=$searchRadius&type=$queryType&key=$API_KEY"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        response.append(line)
                        line = reader.readLine()
                    }
                    reader.close()

                    val jsonObject = JSONObject(response.toString())
                    val results = jsonObject.getJSONArray("results")
                    placesList.clear()
                    for (i in 0 until results.length()) {
                        val placeJson = results.getJSONObject(i)
                        val name = placeJson.optString("name", "Unnamed")
                        val address = placeJson.optString("vicinity", "")
                        val geometry = placeJson.getJSONObject("geometry")
                        val loc = geometry.getJSONObject("location")
                        val lat = loc.getDouble("lat")
                        val lng = loc.getDouble("lng")
                        val distanceArr = FloatArray(1)
                        Location.distanceBetween(userLat, userLng, lat, lng, distanceArr)
                        val distance = distanceArr[0]
                        val place = Place(name, address, lat, lng, queryType.capitalize(), distance)
                        placesList.add(place)
                    }
                    placesList.sortBy { it.distance }
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PlacesListActivity, "Failed to get places: ${connection.responseCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PlacesListActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onItemClick(place: Place) {
        Toast.makeText(this, "Clicked: ${place.name}", Toast.LENGTH_SHORT).show()
        val gmmIntentUri = Uri.parse("google.navigation:q=${place.latitude},${place.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }
}

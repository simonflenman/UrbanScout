package se.umu.sifl0010.googlemapsapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import se.umu.sifl0010.googlemapsapplication.adapter.DrawerListAdapter
import se.umu.sifl0010.googlemapsapplication.model.Place
import se.umu.googlemapsapplication.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // Map and location variables.
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    // Drawer variables.
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle

    // Filters.
    private val selectedCategories = mutableSetOf<String>()
    private val selectedSubcategories = mutableSetOf<String>()
    private var searchRadius: Int = 10000

    // List of places from the Places API.
    private val places = mutableListOf<Place>()

    // Query type (default "restaurant").
    private var queryType: String = "restaurant"

    // Request code for location permission.
    private val LOCATION_PERMISSION_REQUEST = 1

    // API key.
    private val API_KEY = "AIzaSyD8-9Y9KTqL8fTOl6F1JDyGc6yulBZ1k_U"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the Toolbar as ActionBar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup map fragment.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Map fragment not found.", Toast.LENGTH_LONG).show()
        }

        // Initialize the location client.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup DrawerLayout.
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Setup sidebar buttons.
        val btnSearch = drawerLayout.findViewById<Button>(R.id.btnSearch)
        val btnClearFilters = drawerLayout.findViewById<Button>(R.id.btnClearFilters)
        val btnBackDrawer = drawerLayout.findViewById<Button>(R.id.btnBackDrawer)
        btnSearch.setOnClickListener {
            updateMapMarkers()
            drawerLayout.closeDrawers()
        }
        btnClearFilters.setOnClickListener {
            selectedCategories.clear()
            selectedSubcategories.clear()
            queryType = "restaurant" // Reset default.
            Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show()
            updateMapMarkers()
            drawerLayout.closeDrawers()
        }
        btnBackDrawer.setOnClickListener {
            drawerLayout.closeDrawers()
        }

        // Setup FAB to open PlacesListActivity.
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, PlacesListActivity::class.java)
            startActivity(intent)
        }

        // Request location permission if needed.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            getLastKnownLocation()
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        updateMapMarkers()
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = it
                    val latLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    updateMapMarkers()
                }
            }
        }
    }

    // Clears markers, adds the user's marker, then queries nearby places.
    private fun updateMapMarkers() {
        if (!::mMap.isInitialized || currentLocation == null) return
        mMap.clear()
        val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
        queryNearbyPlacesForMap()
    }

    private fun queryNearbyPlacesForMap() {
        if (currentLocation == null) return
        val locationStr = "${currentLocation!!.latitude},${currentLocation!!.longitude}"
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
                    places.clear()
                    for (i in 0 until results.length()) {
                        val placeJson = results.getJSONObject(i)
                        val name = placeJson.optString("name", "Unnamed")
                        val address = placeJson.optString("vicinity", "")
                        val geometry = placeJson.getJSONObject("geometry")
                        val loc = geometry.getJSONObject("location")
                        val lat = loc.getDouble("lat")
                        val lng = loc.getDouble("lng")
                        val distanceArr = FloatArray(1)
                        Location.distanceBetween(
                            currentLocation!!.latitude, currentLocation!!.longitude, lat, lng, distanceArr
                        )
                        val distance = distanceArr[0]
                        val place = Place(name, address, lat, lng, queryType.capitalize(), distance)
                        places.add(place)
                    }
                    places.sortBy { it.distance }
                    withContext(Dispatchers.Main) {
                        for (place in places) {
                            val markerOptions = MarkerOptions()
                                .position(LatLng(place.latitude, place.longitude))
                                .title(place.name)
                                .snippet(place.address)
                            mMap.addMarker(markerOptions)?.tag = place
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Places query failed: ${connection.responseCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
                getLastKnownLocation()
            }
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show()
        }
    }
}

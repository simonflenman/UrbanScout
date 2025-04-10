package se.umu.sifl0010.googlemapsapplication

// … [other imports remain the same]
import se.umu.googlemapsapplication.R

class PlacesListActivity : AppCompatActivity(), PlacesListAdapter.OnItemClickListener {

    // … [other member variables remain the same]

    // Instead of a hardcoded API key, get it from resources.
    private val apiKey: String
        get() = getString(R.string.google_maps_key)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)

        // Setup Toolbar.
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

        // Setup FloatingActionButton for back.
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

        if (userLat == 0.0 && userLng == 0.0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
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

        // Setup sidebar buttons from the drawer.
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

    private fun queryNearbyPlaces() {
        if (userLat == 0.0 && userLng == 0.0) {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show()
            return
        }
        val locationStr = "$userLat,$userLng"
        val urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationStr&radius=$searchRadius&type=$queryType&key=$apiKey"

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

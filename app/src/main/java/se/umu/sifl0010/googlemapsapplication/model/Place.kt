package se.umu.sifl0010.googlemapsapplication.model

/**
 * Data model for a place.
 * Contains name, address, latitude, longitude, category and distance from user.
 */
data class Place(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    var distance: Float = 0f
)

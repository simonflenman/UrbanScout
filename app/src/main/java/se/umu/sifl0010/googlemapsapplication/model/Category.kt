package se.umu.sifl0010.googlemapsapplication.model

/**
 * Data model for a category.
 * Each category has a name and a list of subcategories.
 */
data class Category(
    val name: String,
    val subcategories: List<String>
)

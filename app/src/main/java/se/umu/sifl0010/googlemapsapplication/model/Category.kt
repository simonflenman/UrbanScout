package se.umu.sifl0010.googlemapsapplication.model

/**
 * Recursive data model for a category.
 */
data class Category(
    val name: String,
    val subcategories: List<Category> = emptyList(),
    var isSelected: Boolean = false
)

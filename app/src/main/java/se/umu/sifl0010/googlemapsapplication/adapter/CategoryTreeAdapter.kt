package se.umu.sifl0010.googlemapsapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.umu.googlemapsapplication.R
import se.umu.sifl0010.googlemapsapplication.model.Category

/**
 * A RecyclerView adapter that displays a hierarchical (tree) list of Category objects.
 * Each item shows the category name with an indent based on its level,
 * a checkbox for selection, and, if the category has subcategories, an arrow icon to indicate expandability.
 */
class CategoryTreeAdapter(
    private val rootCategories: List<Category>,
    private val onCategoryChecked: ((Category, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<CategoryTreeAdapter.CategoryViewHolder>() {

    // A flattened list of visible category items.
    data class CategoryItem(
        val category: Category,
        var isExpanded: Boolean = false,
        val level: Int = 0,
        var isChecked: Boolean = false
    )

    private val items = mutableListOf<CategoryItem>()

    init {
        // Add all root items initially.
        rootCategories.forEach { root ->
            items.add(CategoryItem(root, isExpanded = false, level = 0))
        }
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkboxCategory)
        val ivArrow: ImageView = itemView.findViewById(R.id.ivArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        holder.tvCategoryName.text = item.category.name
        // Apply left padding based on the level of the item.
        val leftPadding = 16 * item.level
        holder.tvCategoryName.setPadding(leftPadding, holder.tvCategoryName.paddingTop,
            holder.tvCategoryName.paddingRight, holder.tvCategoryName.paddingBottom)
        // Setup the checkbox.
        holder.checkbox.isChecked = item.isChecked
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onCategoryChecked?.invoke(item.category, isChecked)
        }
        // If there are subcategories, show the arrow.
        if (item.category.subcategories.isNotEmpty()) {
            holder.ivArrow.visibility = View.VISIBLE
            holder.ivArrow.rotation = if (item.isExpanded) 90f else 0f
        } else {
            holder.ivArrow.visibility = View.GONE
        }
        // Set up click listener to expand/collapse (if subcategories exist).
        holder.itemView.setOnClickListener {
            if (item.category.subcategories.isNotEmpty()) {
                if (item.isExpanded) {
                    collapseItem(position)
                } else {
                    expandItem(position)
                }
            }
        }
    }

    private fun expandItem(position: Int) {
        val item = items[position]
        if (!item.isExpanded) {
            item.isExpanded = true
            val subItems = item.category.subcategories.map { subCat ->
                CategoryItem(category = subCat, isExpanded = false, level = item.level + 1)
            }
            items.addAll(position + 1, subItems)
            notifyItemRangeInserted(position + 1, subItems.size)
            notifyItemChanged(position)
        }
    }

    private fun collapseItem(position: Int) {
        val item = items[position]
        if (item.isExpanded) {
            item.isExpanded = false
            var nextIndex = position + 1
            while (nextIndex < items.size && items[nextIndex].level > item.level) {
                nextIndex++
            }
            val count = nextIndex - position - 1
            for (i in 0 until count) {
                items.removeAt(position + 1)
            }
            notifyItemRangeRemoved(position + 1, count)
            notifyItemChanged(position)
        }
    }

    /**
     * Clears all checkbox selections.
     */
    fun clearSelections() {
        items.forEach { it.isChecked = false }
        notifyDataSetChanged()
    }

    /**
     * Retrieves all checked categories.
     */
    fun getCheckedCategories(): List<Category> = items.filter { it.isChecked }.map { it.category }
}

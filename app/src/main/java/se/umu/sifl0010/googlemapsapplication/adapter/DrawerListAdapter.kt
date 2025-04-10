package se.umu.sifl0010.googlemapsapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import se.umu.googlemapsapplication.R

/**
 * Adapter for displaying categories (groups) and subcategories (children)
 * in an ExpandableListView.
 */
class DrawerListAdapter(
    private val context: Context,
    private val listDataHeader: List<String>,
    private val listDataChild: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = listDataHeader.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val header = listDataHeader[groupPosition]
        return listDataChild[header]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any = listDataHeader[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val header = listDataHeader[groupPosition]
        return listDataChild[header]?.get(childPosition) ?: ""
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        val headerTitle = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group, parent, false)
        val lblListHeader = view.findViewById<TextView>(R.id.lblListHeader)
        lblListHeader.text = headerTitle
        return view
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        val childText = getChild(groupPosition, childPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val txtListChild = view.findViewById<TextView>(R.id.lblListItem)
        txtListChild.text = childText
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}

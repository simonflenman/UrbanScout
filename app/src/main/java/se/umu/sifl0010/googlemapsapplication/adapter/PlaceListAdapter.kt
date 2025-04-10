package se.umu.sifl0010.googlemapsapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.umu.googlemapsapplication.R
import se.umu.sifl0010.googlemapsapplication.model.Place

/**
 * RecyclerView adapter to display a list of Place objects.
 */
class PlacesListAdapter(
    private val places: List<Place>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PlacesListAdapter.PlaceViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(place: Place)
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPlaceName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvPlaceAddress)
        val tvDistance: TextView = itemView.findViewById(R.id.tvPlaceDistance)
        val btnNavigate: Button = itemView.findViewById(R.id.btnNavigate)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onItemClick(places[position])
            }
            btnNavigate.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onItemClick(places[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.tvName.text = place.name
        holder.tvAddress.text = place.address
        holder.tvDistance.text = formatDistance(place.distance)
    }

    override fun getItemCount(): Int = places.size

    /**
     * Formats a distance (in meters) as follows:
     * - If under 1000, shows whole meters (e.g. "523 m")
     * - If 1000 or more, shows km and remaining m (e.g. "1 km 200 m")
     */
    private fun formatDistance(distance: Float): String {
        return if (distance < 1000) {
            "${distance.toInt()} m"
        } else {
            val km = distance.toInt() / 1000
            val remaining = distance.toInt() % 1000
            if (remaining > 0)
                "$km km $remaining m"
            else
                "$km km"
        }
    }
}

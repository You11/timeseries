package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.time_series_card.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * here is list with time series. On click they open and show graph with additional info
 */
class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_time_series_button.setOnClickListener {
            val addFragment = AddTimeSeriesFragment()
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, addFragment)
                    .addToBackStack(null)
                    .commit()
        }

        //recycler view from fragment
        time_series_rw.layoutManager = LinearLayoutManager(activity.applicationContext)

        //test data
        val timeSeries = ArrayList<TimeSeries>()
        val db = FirebaseFirestore.getInstance()
        db.collection("time_series")
                .get()
                .addOnCompleteListener {
                    it.result.forEach {
                        val element = TimeSeries(it["name"].toString(),
                                it["creationDate"].toString(),
                                null,
                                null)
                        element.uid = it.id
                        timeSeries.add(element)
                    }

                    time_series_rw.adapter = TimeSeriesRecyclerViewAdapter(timeSeries, this)
                }
                .addOnFailureListener {
                    Toast.makeText(activity.applicationContext, "Failed to load!", Toast.LENGTH_SHORT).show()
                }
    }


    class TimeSeriesRecyclerViewAdapter(private val items: ArrayList<TimeSeries>, private val fragment: Fragment): RecyclerView.Adapter<TimeSeriesRecyclerViewAdapter.ViewHolder>() {

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun bind(timeSeries: TimeSeries) {
                itemView.time_series_name.text = timeSeries.name
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.time_series_card, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.bind(items[position])
            holder?.itemView?.setOnClickListener {
                val newFragment = ViewTimeSeriesFragment()
                val bundle = Bundle()
                bundle.putString("uid", items[position].uid)
                newFragment.arguments = bundle
                fragment.activity.fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .addToBackStack(null)
                        .commit()
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
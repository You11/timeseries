package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.time_series_card.view.*
import java.util.*

/**
 * here is list with time series. On click they open and show graph with additional info
 */
class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
        timeSeries.add(TimeSeries("у", null, null, null, null))
        timeSeries.add(TimeSeries("gavgav", null, null, null, null))
        time_series_rw.adapter = TimeSeriesRecyclerViewAdapter(timeSeries)

        super.onViewCreated(view, savedInstanceState)
    }
}


class TimeSeriesRecyclerViewAdapter(val items: ArrayList<TimeSeries>): RecyclerView.Adapter<TimeSeriesRecyclerViewAdapter.ViewHolder>() {

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
    }

    override fun getItemCount(): Int = items.size
}
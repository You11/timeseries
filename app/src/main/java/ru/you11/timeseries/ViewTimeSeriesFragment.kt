package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.time_series_card.*
import kotlinx.android.synthetic.main.view_time_series_fragment.*

/**
 * Created by you11 on 18.01.2018.
 */
class ViewTimeSeriesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.view_time_series_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseFirestore.getInstance().collection("time_series").document(arguments.getString("uid")).get()
                .addOnCompleteListener {
                    view_time_series_name.text = it.result["name"].toString()
                    view_time_series_creation_date.text = it.result["creationDate"].toString()
                    val entries = ArrayList<Entry>()
                    val data = it.result["dataValues"] as HashMap<*, *>
                    data.forEach {
                        val values = it.value as List<*>
                        entries.add(Entry(values[0].toString().toFloat(), values[1].toString().toFloat()))
                    }

                    val dataSet = LineDataSet(entries, it.result["dataDescription"].toString())
                    view_time_series_chart.data = LineData(dataSet)
                    view_time_series_chart.invalidate()
                }
                .addOnFailureListener {
                    Toast.makeText(activity.applicationContext, "Failed to load data!", Toast.LENGTH_SHORT).show()
                }

    }
}
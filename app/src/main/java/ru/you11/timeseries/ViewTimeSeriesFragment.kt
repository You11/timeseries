package ru.you11.timeseries

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
                    //points array
                    val entries = ArrayList<Entry>()
                    val data = it.result["dataValues"] as HashMap<*, *>
                    data.forEach {
                        val values = it.value as List<*>
                        entries.add(Entry(values[0].toString().toFloat(), values[1].toString().toFloat()))
                    }

                    val dataSet = LineDataSet(entries, it.result["dataDescription"].toString())
                    //chart style
                    dataSet.lineWidth = 1.5f
                    dataSet.color = Color.BLUE
                    dataSet.valueTextSize = 14f
                    view_time_series_chart.data = LineData(dataSet)
                    view_time_series_chart.axisRight.isEnabled = false
                    view_time_series_chart.axisLeft.textSize = 14f
                    view_time_series_chart.axisLeft.axisLineWidth = 1.25f
                    view_time_series_chart.axisLeft.gridColor = Color.parseColor("#7986CB")
                    view_time_series_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    view_time_series_chart.xAxis.textSize = 14f
                    view_time_series_chart.xAxis.axisLineWidth = 1.25f
                    view_time_series_chart.xAxis.gridColor = Color.parseColor("#7986CB")
                    //because label is clipped
                    view_time_series_chart.extraBottomOffset = 5f
                    view_time_series_chart.description.isEnabled = false
                    view_time_series_chart.legend.isEnabled = false
                    view_time_series_chart.invalidate()
                    view_time_series_chart.visibility = View.VISIBLE

                    //add descriptions to axis
                    view_time_series_x_axis_description.append(it.result["dataDescription"].toString())
                    view_time_series_x_axis_description.visibility = View.VISIBLE
                    view_time_series_y_axis_description.append(it.result["timeDescription"].toString())
                    view_time_series_y_axis_description.visibility = View.VISIBLE
                    view_screen_loading_icon.hide()

                }
                .addOnFailureListener {
                    Toast.makeText(activity.applicationContext, "Failed to load data!", Toast.LENGTH_SHORT).show()
                    view_screen_loading_icon.hide()
                }
    }
}
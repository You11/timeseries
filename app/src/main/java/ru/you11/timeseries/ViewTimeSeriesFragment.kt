package ru.you11.timeseries

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

                    val timeSeries = TimeSeries(it.result["name"].toString(),
                            it.result["creationDate"].toString(),
                            null,
                            it.result["dataValues"] as HashMap<String, List<Double>>)
                    timeSeries.dataDescription = it.result["dataDescription"].toString()
                    timeSeries.timeDescription = it.result["timeDescription"].toString()
                    timeSeries.uid = arguments.getString("uid")

                    view_time_series_name.text = timeSeries.name
                    view_time_series_creation_date.text = timeSeries.creationDate

                    setupCharts(timeSeries)

                    val id = timeSeries.uid
                    if (id != null) {
                        setupEditButton(id)
                    }

                    setupDeleteButton()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Failed to load data!", Toast.LENGTH_SHORT).show()
                    view_screen_loading_icon.hide()
                }
    }

    private fun setupEditButton(id: String) {
        view_time_series_edit_button.visibility = View.VISIBLE
        view_time_series_edit_button.setOnClickListener {
            val fragment = AddTimeSeriesFragment()
            //maybe should serialize
            val bundle = Bundle()
            bundle.putString("editTSid", id)
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            fragment.arguments = bundle
        }
    }

    private fun setupDeleteButton() {
        //setup delete button
        view_time_series_delete_button.visibility = View.VISIBLE
        view_time_series_delete_button.setOnClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Confirm")
                    .setMessage("Do you want to delete this time series?")
                    .setPositiveButton("Ok", { dialog, which ->
                        //deletes entry from firestore and exits
                        FirebaseFirestore.getInstance().collection("time_series")
                                .document(arguments.getString("uid"))
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(activity, "Deleted!", Toast.LENGTH_SHORT).show()
                                    fragmentManager.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, "Error: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                    })
                    .setNegativeButton("Cancel", { dialog, which ->

                    })
            alertDialog.show()
        }
    }

    private fun setupCharts(timeSeries: TimeSeries) {
        //points array
        val entries = ArrayList<Entry>()
        timeSeries.dataValues?.forEach {
            val values = it.value as List<*>
            entries.add(Entry(values[0].toString().toFloat(), values[1].toString().toFloat()))
        }

        val dataSet = LineDataSet(entries, timeSeries.dataDescription)
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
        view_time_series_x_axis_description.append(timeSeries.dataDescription)
        view_time_series_x_axis_description.visibility = View.VISIBLE
        view_time_series_y_axis_description.append(timeSeries.timeDescription)
        view_time_series_y_axis_description.visibility = View.VISIBLE
        view_screen_loading_icon.hide()
    }
}
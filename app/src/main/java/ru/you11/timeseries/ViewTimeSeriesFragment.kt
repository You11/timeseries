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
                    view_time_series_name.text = it.result["name"].toString()
                    view_time_series_creation_date.text = it.result["creationDate"].toString()

                    setupCharts(it)

                    view_time_series_edit_button.visibility = View.VISIBLE
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
                .addOnFailureListener {
                    Toast.makeText(activity, "Failed to load data!", Toast.LENGTH_SHORT).show()
                    view_screen_loading_icon.hide()
                }
    }

    private fun setupCharts(task: Task<DocumentSnapshot>) {
        //points array
        val entries = ArrayList<Entry>()
        val data = task.result["dataValues"] as HashMap<*, *>
        data.forEach {
            val values = it.value as List<*>
            entries.add(Entry(values[0].toString().toFloat(), values[1].toString().toFloat()))
        }

        val dataSet = LineDataSet(entries, task.result["dataDescription"].toString())
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
        view_time_series_x_axis_description.append(task.result["dataDescription"].toString())
        view_time_series_x_axis_description.visibility = View.VISIBLE
        view_time_series_y_axis_description.append(task.result["timeDescription"].toString())
        view_time_series_y_axis_description.visibility = View.VISIBLE
        view_screen_loading_icon.hide()
    }
}
package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_time_series_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by you11 on 17.01.2018.
 */
class AddTimeSeriesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.add_time_series_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPointsToLayout(add_ts_points_layout)

        add_ts_points_more_button.setOnClickListener {
            addPointsToLayout(add_ts_points_layout)
        }

        save_ts_button.setOnClickListener {

            //check if name is blank
            if (add_ts_name_value.text.isNullOrBlank()) {
                Toast.makeText(activity, "Please enter name of time series", Toast.LENGTH_SHORT).show()
                add_ts_name_value.requestFocus()
                return@setOnClickListener
            }

            val dataPoints = HashMap<String, List<Double>>()
            for (i in 0 until add_ts_points_layout.childCount) {
                val secLayout = add_ts_points_layout.getChildAt(i) as LinearLayout
                val dataValueView = secLayout.getChildAt(0) as EditText
                val timeValueView = secLayout.getChildAt(1) as EditText

                //check if they are blank
                if (dataValueView.text.isNullOrBlank()) {
                    Toast.makeText(activity, "Please enter data value of time series", Toast.LENGTH_SHORT).show()
                    dataValueView.requestFocus()
                    return@setOnClickListener
                }
                if (timeValueView.text.isNullOrBlank()) {
                    Toast.makeText(activity, "Please enter time value of time series", Toast.LENGTH_SHORT).show()
                    timeValueView.requestFocus()
                    return@setOnClickListener
                }

                //check if input is number
                val arr = ArrayList<Double>()
                val pattern = "^-?\\d*\\.?\\d+\$"
                val dataValue = dataValueView.text.toString()
                if (Regex(pattern).matches(dataValue)) {
                    arr.add(dataValue.toDouble())
                } else {
                    Toast.makeText(activity, "Please enter valid input", Toast.LENGTH_SHORT).show()
                    dataValueView.requestFocus()
                    return@setOnClickListener
                }
                val timeValue = timeValueView.text.toString()
                if (Regex(pattern).matches(timeValue)) {
                    arr.add(timeValue.toDouble())
                } else {
                    Toast.makeText(activity, "Please enter valid input", Toast.LENGTH_SHORT).show()
                    timeValueView.requestFocus()
                    return@setOnClickListener
                }

                dataPoints[i.toString()] = arr
            }

            val ts = TimeSeries(add_ts_name_value.text.toString(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Calendar.getInstance().time),
                    null,
                    dataPoints)
            add_ts_data_description_value?.text.toString().let {
                ts.dataDescription = it
            }
            add_ts_time_description_value.text.toString().let {
                ts.timeDescription = it
            }

            //TODO: hide keyboard

            //send data to firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("time_series")
                    .add(ts)
                    .addOnCompleteListener {
                        activity.fragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    //adds value and time edittexts to layout in xml file
    private fun addPointsToLayout(layout: LinearLayout) {
        val newLayout = LinearLayout(activity)
        newLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        newLayout.orientation = LinearLayout.HORIZONTAL
        val value = EditText(activity)
        val time = EditText(activity)
        value.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        value.hint = "data value"
        time.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        time.hint = "time value"
        val deleteButton = Button(activity)
        deleteButton.text = "Delete"
        deleteButton.setOnClickListener {
            layout.removeView(newLayout)
        }
        newLayout.addView(value)
        newLayout.addView(time)
        newLayout.addView(deleteButton)
        layout.addView(newLayout)
    }
}

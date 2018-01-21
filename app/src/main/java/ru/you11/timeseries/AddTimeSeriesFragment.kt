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
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.add_time_series_fragment.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Screen for adding new time series. Name and points are required, descriptions are not.
 * Data are sent to firestore. Also this fragments servers for editing existing time series from
 * ViewTimeSeriesFragment.
 */
class AddTimeSeriesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.add_time_series_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEdit()) {
            fillExistingValues(arguments.getString("editTSid"))
        } else {
            //adds first set of points
            addPointsToLayout(null)
        }

        //"Add More" button
        add_ts_points_more_button.setOnClickListener {
            addPointsToLayout(null)
        }

        //Save and exit
        setupSaveButton()
    }

    private fun isEdit() = arguments != null && arguments.getString("editTSid") != null


    private fun fillExistingValues(id: String) {
        FirebaseFirestore.getInstance().collection("time_series").document(id).get()
                .addOnCompleteListener {
                    val timeSeries = TimeSeries(it.result["name"].toString(),
                            it.result["creationDate"].toString(),
                            null,
                            it.result["dataValues"] as HashMap<String, List<Double>>)
                    timeSeries.dataDescription = it.result["dataDescription"].toString()
                    timeSeries.timeDescription = it.result["timeDescription"].toString()
                    add_ts_name_value.setText(timeSeries.name)
                    add_ts_time_description_value.setText(timeSeries.timeDescription)
                    add_ts_data_description_value.setText(timeSeries.dataDescription)
                    timeSeries.dataValues?.forEach {
                        addPointsToLayout(it.value)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
    }


    private fun setupSaveButton() {
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
            add_ts_data_description_value.text.toString().let {
                ts.dataDescription = it
            }
            add_ts_time_description_value.text.toString().let {
                ts.timeDescription = it
            }

            //TODO: hide keyboard

            //send data to firestore
            val db = FirebaseFirestore.getInstance()

            if (isEdit()) {
                db.collection("time_series").document(arguments.getString("editTSid"))
                        .set(ts, SetOptions.merge())
                        .addOnCompleteListener {
                            fragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            showErrorMessage(it)
                            fragmentManager.popBackStack()
                        }
            } else {
                db.collection("time_series")
                        .add(ts)
                        .addOnCompleteListener {
                            fragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            showErrorMessage(it)
                            fragmentManager.popBackStack()
                        }
            }
        }
    }

    private fun showErrorMessage(it: Exception) {
        Toast.makeText(activity, "Error: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    //adds value and time edittexts to layout in xml file
    private fun addPointsToLayout(defaultValues: List<Double>?) {
        val newLayout = LinearLayout(activity)
        newLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        newLayout.orientation = LinearLayout.HORIZONTAL

        val value = EditText(activity)
        val time = EditText(activity)
        value.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        value.hint = "data value"
        time.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        time.hint = "time value"
        if (defaultValues != null) {
            value.setText(defaultValues[0].toString())
            time.setText(defaultValues[1].toString())
        }

        //deletes this layout
        val deleteButton = Button(activity)
        deleteButton.text = "Delete"
        deleteButton.setOnClickListener {
            add_ts_points_layout.removeView(newLayout)
        }

        newLayout.addView(value)
        newLayout.addView(time)
        newLayout.addView(deleteButton)
        add_ts_points_layout.addView(newLayout)
    }
}

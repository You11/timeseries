package ru.you11.timeseries

import android.app.Fragment
import android.graphics.Color
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

        //loads pre-existing values if it is edit
        if (isEdit()) {
            fillExistingValues(arguments.getString("editTSid"))
        } else {
            //adds first set of points
            addXYPointsToLayout(null)
        }

        //"Add More" button
        add_ts_add_more_button.setOnClickListener {
            addXYPointsToLayout(null)
        }

        //Save and exit
        setupSaveButton()
    }


    private fun isEdit() = arguments != null && arguments.getString("editTSid") != null


    //fills fields with values from firestore
    private fun fillExistingValues(id: String) {
        FirebaseFirestore.getInstance().collection("time_series").document(id).get()
                .addOnCompleteListener {
                    val timeSeries = TimeSeries(it.result["name"].toString(),
                            it.result["creation_date"].toString(),
                            it.result["data_values"] as HashMap<String, List<Double>>,
                            it.result["x_axis_description"].toString(),
                            it.result["y_axis_description"].toString())

                    add_ts_name_value.setText(timeSeries.name)
                    add_ts_x_axis_description_value.setText(timeSeries.xAxisDescription)
                    add_ts_y_axis_description_value.setText(timeSeries.yAxisDescription)

                    timeSeries.dataValues?.forEach {
                        addXYPointsToLayout(it.value)
                    }
                }
                .addOnFailureListener {
                    showErrorMessage(it)
                }
    }


    private fun setupSaveButton() {
        add_ts_save_button.setOnClickListener {

            //x, y points of time series
            val dataPoints = HashMap<String, List<Double>>()
            if (!checkInputForValidation(dataPoints)) return@setOnClickListener

            val ts = TimeSeries(add_ts_name_value.text.toString(),
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Calendar.getInstance().time),
                    dataPoints,
                    add_ts_x_axis_description_value.text.toString(),
                    add_ts_y_axis_description_value.text.toString())

            //TODO: hide keyboard

            //send data to firestore
            val db = FirebaseFirestore.getInstance()

            val tsFirestore = HashMap<String, Any>()
            tsFirestore["name"] = ts.name
            tsFirestore["creation_date"] = ts.creationDate
            ts.xAxisDescription?.let {
                tsFirestore["x_axis_description"] = it
            }
            ts.yAxisDescription?.let {
                tsFirestore["y_axis_description"] = it
            }
            ts.dataValues?.let {
                tsFirestore["data_values"] = it
            }

            if (isEdit()) {
                db.collection("time_series").document(arguments.getString("editTSid"))
                        .set(tsFirestore, SetOptions.merge())
                        .addOnCompleteListener {
                            fragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            showErrorMessage(it)
                            fragmentManager.popBackStack()
                        }
            } else {
                db.collection("time_series")
                        .add(tsFirestore)
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

    private fun checkInputForValidation(dataPoints: HashMap<String, List<Double>>): Boolean {
        //check if name is blank
        if (add_ts_name_value.text.isNullOrBlank()) {
            Toast.makeText(activity, getString(R.string.add_ts_input_name_help), Toast.LENGTH_SHORT).show()
            add_ts_name_value.requestFocus()
            return false
        }

        for (i in 0 until add_ts_add_points_layout.childCount) {
            val secLayout = add_ts_add_points_layout.getChildAt(i) as LinearLayout
            val xValueView = secLayout.getChildAt(0) as EditText
            val yValueView = secLayout.getChildAt(1) as EditText

            //check if they are blank
            if (xValueView.text.isNullOrBlank()) {
                Toast.makeText(activity, getString(R.string.add_ts_input_x_help_text), Toast.LENGTH_SHORT).show()
                xValueView.requestFocus()
                return false
            }

            if (yValueView.text.isNullOrBlank()) {
                Toast.makeText(activity, getString(R.string.add_ts_input_y_help_text), Toast.LENGTH_SHORT).show()
                yValueView.requestFocus()
                return false
            }

            //check if input is number
            val arr = ArrayList<Double>()
            val pattern = "^-?\\d*\\.?\\d+\$"

            fun isNumber(valueView: EditText): Boolean {
                val value = valueView.text.toString()
                if (Regex(pattern).matches(value)) {
                    arr.add(value.toDouble())
                } else {
                    Toast.makeText(activity, getString(R.string.add_ts_incorrect_input_message), Toast.LENGTH_SHORT).show()
                    valueView.requestFocus()
                    return true
                }

                return false
            }

            if (isNumber(xValueView)) return false

            if (isNumber(yValueView)) return false

            dataPoints[i.toString()] = arr
        }

        return true
    }


    private fun showErrorMessage(it: Exception) {
        Toast.makeText(activity, getString(R.string.error_with_localized_message) + it.localizedMessage, Toast.LENGTH_SHORT).show()
    }


    //adds value and time edittexts to layout in xml file
    private fun addXYPointsToLayout(defaultValues: List<Double>?) {

        val newLayout = LinearLayout(activity)

        //main layout style
        newLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        newLayout.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        newLayout.layoutParams = layoutParams

        //both axis values
        val xValue = EditText(activity)
        val yValue = EditText(activity)

        //edit text style
        xValue.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        xValue.hint = getString(R.string.add_ts_x_input_hint)
        yValue.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        yValue.hint = getString(R.string.add_ts_y_input_hint)

        if (defaultValues != null) {
            xValue.setText(defaultValues[0].toString())
            yValue.setText(defaultValues[1].toString())
        }

        //deletes this layout
        val deleteButton = Button(activity)

        //delete button style
        val deleteButtonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        deleteButton.layoutParams = deleteButtonParams
        deleteButtonParams.leftMargin = 10
        deleteButtonParams.rightMargin = 10
        deleteButton.text = getString(R.string.add_ts_delete_button_text)
        deleteButton.setBackgroundColor(Color.parseColor("#EF5350"))
        deleteButton.setPadding(10, 0, 10, 0)

        deleteButton.setOnClickListener {
            add_ts_add_points_layout.removeView(newLayout)
        }

        //add all of them to parent
        newLayout.addView(xValue)
        newLayout.addView(yValue)
        newLayout.addView(deleteButton)
        add_ts_add_points_layout.addView(newLayout)
    }
}

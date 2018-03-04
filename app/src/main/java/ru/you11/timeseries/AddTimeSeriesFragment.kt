package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.add_time_series_fragment.*
import kotlinx.android.synthetic.main.add_time_series_points_layout.view.*
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

        add_ts_add_points_rw.layoutManager = LinearLayoutManager(activity)

        val items = getExistingValues()
        add_ts_add_points_rw.adapter = AddTimeSeriesRecyclerViewAdapter(items)

        //"Add More" button
        add_ts_add_more_button.setOnClickListener {
            addItem(items)
        }

        //Save and exit
        setupSaveButton()
    }

    private fun addItem(items: ArrayList<List<Float>?>) {
        items.add(null)
        add_ts_add_points_rw.adapter.notifyDataSetChanged()
    }

    private fun getExistingValues(): ArrayList<List<Float>?> {
        val defaultItems = ArrayList<List<Float>?>()

        if (isEdit()) {
            fillExistingValues(arguments.getString("editTSid"), defaultItems)
        } else {
            defaultItems.add(null)
        }

        return defaultItems
    }


    private class AddTimeSeriesRecyclerViewAdapter(private val items: ArrayList<List<Float>?>):
            RecyclerView.Adapter<AddTimeSeriesFragment.AddTimeSeriesRecyclerViewAdapter.ViewHolder>() {

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun bind(values: List<Float>) {
                itemView.add_ts_x_value.setText(values[0].toString())
                itemView.add_ts_y_value.setText(values[1].toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.add_time_series_points_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val currentItem = items[position]
            if (currentItem != null)
                holder?.bind(currentItem)

            val filter = RegexInputFilter("^-?\\d{0,4}(\\.\\d{0,3})?\$")
            holder?.itemView?.add_ts_x_value?.filters = arrayOf(filter)
            holder?.itemView?.add_ts_y_value?.filters = arrayOf(filter)

            holder?.itemView?.add_ts_button_delete?.setOnClickListener {
                removeAt(position)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        private fun removeAt(position: Int) {
            if (position < items.size)
                items.removeAt(position)
            notifyDataSetChanged()
            //TODO: notifyItemRemoved not working correctly here
        }
    }


    private fun isEdit() = arguments != null && arguments.getString("editTSid") != null


    //fills fields with values from firestore
    private fun fillExistingValues(id: String, items: ArrayList<List<Float>?>) {
        FirebaseFirestore.getInstance().collection("time_series").document(id).get()
                .addOnCompleteListener {
                    val timeSeries = TimeSeries(it.result["name"].toString(),
                            it.result["creation_date"].toString(),
                            it.result["data_values"] as HashMap<String, List<Float>>,
                            it.result["x_axis_description"].toString(),
                            it.result["y_axis_description"].toString())

                    add_ts_name_value.setText(timeSeries.name)
                    add_ts_x_axis_description_value.setText(timeSeries.xAxisDescription)
                    add_ts_y_axis_description_value.setText(timeSeries.yAxisDescription)

                    //adds later data values to array in onViewCreated
                    timeSeries.dataValues?.forEach {
                        items.add(it.value)
                    }

                    if (add_ts_add_points_rw.adapter != null)
                        add_ts_add_points_rw.adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    showErrorMessage(it)
                }
    }


    private fun setupSaveButton() {
        add_ts_save_button.setOnClickListener {

            //x, y points of time series
            val dataPoints = HashMap<String, List<Float>>()
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

    private fun checkInputForValidation(dataPoints: HashMap<String, List<Float>>): Boolean {
        //check if name is blank
        if (add_ts_name_value.text.isNullOrBlank()) {
            Toast.makeText(activity, getString(R.string.add_ts_input_name_help), Toast.LENGTH_SHORT).show()
            add_ts_name_value.requestFocus()
            return false
        }

        for (i in 0 until add_ts_add_points_rw.childCount) {
            val secLayout = add_ts_add_points_rw.getChildAt(i) as LinearLayout
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
            //TODO: maybe should redo this
            val arr = ArrayList<Float>()

            fun checkEditTextInput(valueView: EditText): Boolean {
                val value = valueView.text.toString()
                val result = Validation().isNumber(value)
                return if (result) {
                    arr.add(value.toFloat())
                    true
                } else {
                    //if it's not valid
                    Toast.makeText(activity, getString(R.string.add_ts_incorrect_input_message), Toast.LENGTH_SHORT).show()
                    valueView.requestFocus()
                    false
                }
            }

            if (!checkEditTextInput(xValueView)) return false

            if (!checkEditTextInput(yValueView)) return false

            dataPoints[i.toString()] = arr
        }

        return true
    }


    private fun showErrorMessage(it: Exception) {
        Toast.makeText(activity, getString(R.string.error_with_localized_message) + it.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    class RegexInputFilter(private val pattern: String): InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            return if (Regex(pattern).matches(source.toString() + dest.toString())) {
                null
            } else ""
        }
    }
}

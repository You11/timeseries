package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            val dataPoints = HashMap<String, List<Double>>()
            for (i in 0 until add_ts_points_layout.childCount) {
                val secLayout = add_ts_points_layout.getChildAt(i) as LinearLayout
                val firstValue = secLayout.getChildAt(0) as EditText
                val secondValue = secLayout.getChildAt(1) as EditText
                val arr = ArrayList<Double>()
                arr.add(firstValue.text.toString().toDouble())
                arr.add(secondValue.text.toString().toDouble())
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

            //should hide keyboard, but it doesn't, maybe it's emulator fault
//            if (activity.currentFocus != null) {
//                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromInputMethod(activity.currentFocus.windowToken, 0)
//            }

            //send data to firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("time_series")
                    .add(ts)
                    .addOnCompleteListener {
                        activity.fragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity.applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    //adds value and time edittexts to layout in xml file
    private fun addPointsToLayout(layout: LinearLayout) {
        val context = activity.applicationContext
        val newLayout = LinearLayout(context)
        newLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        newLayout.orientation = LinearLayout.HORIZONTAL
        val value = EditText(context)
        val time = EditText(context)
        newLayout.addView(value)
        newLayout.addView(time)
        layout.addView(newLayout)
    }
}

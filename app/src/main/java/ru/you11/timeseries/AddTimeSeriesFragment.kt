package ru.you11.timeseries

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_time_series_fragment.*

/**
 * Created by you11 on 17.01.2018.
 */
class AddTimeSeriesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.add_time_series_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        save_ts_button.setOnClickListener {
            val ts = TimeSeries(add_ts_name_value.text.toString(),
                    null,
                    null,
                    null,
                    null)
            //should hide keyboard, but doesn't, maybe emulator fault
//            if (activity.currentFocus != null) {
//                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromInputMethod(activity.currentFocus.windowToken, 0)
//            }

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
}
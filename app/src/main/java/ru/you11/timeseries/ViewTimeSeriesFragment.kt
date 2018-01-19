package ru.you11.timeseries

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

//        val db = FirebaseFirestore.getInstance()
//        val data = db.collection("time_series").whereEqualTo("uid", arguments.getString("uid")).get().result.documents[0]
//        val timeSeries = TimeSeries(data["name"].toString(),
//                data["creationDate"].toString(),
//                data["changeDate"].toString(),
//                data["dataValues"],
//                data["timeValues"])

        view_time_series_name.text = arguments.getString("uid")
    }
}
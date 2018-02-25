package ru.you11.timeseries

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by you11 on 17.01.2018.
 */
class TimeSeries(var name: String,
                 val creationDate: String,
                 val dataValues: HashMap<String, List<Float>>?,
                 val xAxisDescription: String? = null,
                 val yAxisDescription: String? = null) {

    var uid: String? = null
}

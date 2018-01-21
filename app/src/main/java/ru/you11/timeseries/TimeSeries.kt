package ru.you11.timeseries

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by you11 on 17.01.2018.
 */
class TimeSeries(var name: String,
                 val creationDate: String,
                 var dataValues: HashMap<String, List<Double>>?) {

    var dataDescription: String? = null
    var timeDescription: String? = null
    var uid: String? = null
}
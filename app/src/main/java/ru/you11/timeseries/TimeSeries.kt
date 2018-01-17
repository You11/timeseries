package ru.you11.timeseries

import java.util.*

/**
 * Created by you11 on 17.01.2018.
 */
class TimeSeries(var name: String,
                 val creationDate: Date?,
                 var changeDate: Date?,
                 var dataValues: Array<Double>?,
                 var timeValues: Array<Double>?) {

    var dataDescription: String? = null
    var timeDescription: String? = null
}
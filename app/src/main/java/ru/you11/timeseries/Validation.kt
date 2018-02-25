package ru.you11.timeseries

import android.widget.Toast

/**
 * Created by you11 on 24.02.2018.
 */
class Validation {

    fun isNumber(input: String): Boolean {
        val pattern = "^-?\\d{0,4}(\\.\\d{0,3})?\$"

        //TODO: add to regex empty string
        if (input == "") return false

        return Regex(pattern).matches(input)
    }
}
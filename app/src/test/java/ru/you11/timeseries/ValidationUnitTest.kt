package ru.you11.timeseries

import android.support.test.filters.SmallTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by you11 on 24.02.2018.
 */
@SmallTest
class ValidationUnitTest {

    @Test
    fun validationNumber_isCorrect() {
        assertTrue(Validation().isNumber("23"))
    }

    @Test
    fun validationNumberWithDot_isCorrect() {
        assertTrue(Validation().isNumber("23.5"))
    }

    @Test
    fun validationNumberWithMinus_isCorrect() {
        assertTrue(Validation().isNumber("-23"))
    }

    @Test
    fun validationNumberWithMinusAndDot_isCorrect() {
        assertTrue(Validation().isNumber("-23.5"))
    }

    @Test
    fun validationNumberWithoutInteger_isCorrect() {
        assertTrue(Validation().isNumber("-.5"))
    }

    @Test
    fun validationNumberWithString_isFalse() {
        assertFalse(Validation().isNumber("-.m5"))
    }

    @Test
    fun validationNumberLessThan6Digits_isFalse() {
        assertFalse(Validation().isNumber("10000"))
    }

    @Test
    fun validationNumberDivisionalLessThan4Digits_isFalse() {
        assertFalse(Validation().isNumber("0.33333"))
    }

    @Test fun validationNumberEmpty_isFalse() {
        assertFalse(Validation().isNumber(""))
    }
}
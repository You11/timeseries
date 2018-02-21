package ru.you11.timeseries

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by you11 on 20.02.2018.
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityUnitTest {

    @Test
    fun isEquals4() {
        Assert.assertEquals(4, 2 + 2)
    }
}
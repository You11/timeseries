package ru.you11.timeseries

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by you11 on 25.02.2018.
 */
@MediumTest
class AddTimeSeriesInstrumentationText {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        activityTestRule.activity.fragmentManager.beginTransaction().replace(R.id.main_fragment_container, AddTimeSeriesFragment()).commit()
    }

    @Test fun nameEditText_isDisplayed() {
        onView(withId(R.id.add_ts_name_value)).check(matches(isDisplayed()))
    }

    @Test fun xDescriptionEditText_isDisplayed() {
        onView(withId(R.id.add_ts_x_axis_description_value)).check(matches(isDisplayed()))
    }

    @Test fun yDescriptionEditText_isDisplayed() {
        onView(withId(R.id.add_ts_y_axis_description_value)).check(matches(isDisplayed()))
    }

    @Test fun pointsLayout_isDisplayed() {
        onView(allOf(withParent(withId(R.id.add_ts_add_points_layout))))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun deleteButton_isDisplayed() {

    }

    @Test fun validateNameEditText() {
        onView(withId(R.id.add_ts_name_value)).perform(typeText("Hello"))
                .perform(click())
                .check(matches(withText("Hello")))
    }

    @Test fun validateXDescriptionEditText() {
        onView(withId(R.id.add_ts_x_axis_description_value)).perform(typeText("Hello"))
                .perform(click())
                .check(matches(withText("Hello")))
    }

    @Test fun validateYDescriptionEditText() {
        onView(withId(R.id.add_ts_y_axis_description_value)).perform(typeText("Hello"))
                .perform(click())
                .check(matches(withText("Hello")))
    }

}
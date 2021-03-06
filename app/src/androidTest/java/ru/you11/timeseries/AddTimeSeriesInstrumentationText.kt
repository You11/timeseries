package ru.you11.timeseries

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Matchers.*
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

    @Test fun xEditText_isDisplayed() {
        onView(withId(R.id.add_ts_x_value))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun yEditText_isDisplayed() {
        onView(withId(R.id.add_ts_y_value))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun deleteButton_isDisplayed() {
        onView(allOf(withId(R.id.add_ts_delete_row_button)))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun addButton_isDisplayed() {
        onView(withId(R.id.add_ts_add_more_button))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun saveButton_isDisplayed() {
        onView(withId(R.id.add_ts_save_button))
                .check(matches(isDisplayed()))
                .perform(click())
    }

    @Test fun validateNameEditText() {
        onView(withId(R.id.add_ts_name_value)).perform(typeText("Hello"))
                .perform(click())
                .check(matches(withText("Hello")))
    }

    @Test fun validateXValueEditText() {
        onView(withId(R.id.add_ts_x_value)).perform(typeText("356.654"))
                .perform(click())
                .check(matches(withText("356.654")))
    }

    @Test fun validateYValueEditText() {
        onView(withId(R.id.add_ts_y_value)).perform(typeText("356.654"))
                .perform(click())
                .check(matches(withText("356.654")))
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

    @Test fun addMoreButtonCreatesNewRow() {
        onView(withId(R.id.add_ts_add_more_button)).perform(click())

        onView(withId(R.id.add_ts_add_points_rw)).check(matches(hasChildCount(2)))

        onView(allOf(withParent(withId(R.id.add_ts_add_points_rw)), withParentIndex(1))).check(matches(isDisplayed()))
    }

    @Test fun deleteButtonDeletesRow() {
        onView(withId(R.id.add_ts_delete_row_button)).perform(click())

        onView(withId(R.id.add_ts_add_points_rw)).check(matches(hasChildCount(0)))
    }
}
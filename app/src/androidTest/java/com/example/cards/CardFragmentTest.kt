package com.example.cards

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import androidx.test.core.app.ActivityScenario
import org.junit.Before
import org.junit.Test

class CardFragmentTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun clickRandomDeckButton() {
        onView(withId(R.id.random_button)).perform(click())
        Thread.sleep(3600)
        onView(withId(R.id.card_elixir)).check(matches(isDisplayed()))
        onView(withId(R.id.card_image)).check(matches(isDisplayed()))
        onView(withId(R.id.card_list)).check(matches(isDisplayed()))
        onView(withId(R.id.card_lvl)).check(matches(isDisplayed()))
    }
}
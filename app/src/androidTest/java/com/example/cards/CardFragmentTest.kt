package com.example.cards

import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test


private const val TIME_TO_LOAD_CARDS = 4600L
private const val TIME_TO_CHANGE_CARD = 2600L

class CardFragmentTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.random_button)).perform(click())
        Thread.sleep(TIME_TO_LOAD_CARDS)
    }

    @Test
    fun randomDeckButton() {
        onView(withId(R.id.card_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
            .check(matches(isDisplayed()))

        onView(withId(R.id.random_button)).check(matches(isNotEnabled()))

        Thread.sleep(TIME_TO_LOAD_CARDS)

        onView(withId(R.id.random_button)).check(matches(isEnabled()))

        onView(withId(R.id.random_button)).perform(click())

        Thread.sleep(TIME_TO_LOAD_CARDS)

        onView(withId(R.id.random_button)).check(matches(isEnabled()))

    }

    @Test
    fun changeCards() {
        for (i in 0..7) {
            onView(withId(R.id.card_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(i, click()))
            Thread.sleep(TIME_TO_CHANGE_CARD)
        }
    }

    @Test
    fun dragAndDrop() {
        onView(withId(R.id.card_list))
            .perform(dragAndMoveRightAndAssertMove(100f, 1f))
    }

    private fun createScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun dragAndMoveRightAndAssertMove(x: Float, y: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>  =
                Matchers.allOf(
                    isAssignableFrom(RecyclerView::class.java),
                    isDisplayed()
                )

            override fun getDescription(): String = String()

            override fun perform(uiController: UiController, view: View) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val screenShotBefore = createScreenShot(view).getPixel(view.x.toInt(), view.y.toInt())

                val coordinates = floatArrayOf(x + location[0], y + location[1])
                val toCoordinates = floatArrayOf(x + location[0] + 450f, y + location[1])
                val precision = floatArrayOf(1f, 1f)

                val down: MotionEvent = MotionEvents.sendDown(uiController, coordinates, precision).down
                uiController.loopMainThreadForAtLeast(2000)
                MotionEvents.sendDown(uiController, coordinates, precision).longPress
                MotionEvents.sendMovement(uiController, down, toCoordinates)
                MotionEvents.sendUp(uiController, down, coordinates)

                val screenShotAfter = createScreenShot(view).getPixel(view.x.toInt(), view.y.toInt())

                Assert.assertNotEquals(screenShotBefore, screenShotAfter)
            }
        }
    }
}
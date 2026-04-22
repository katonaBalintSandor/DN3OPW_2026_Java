package com.example.dn3opw_2026

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dn3opw_2026.activities.RegisterActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterFlowTest {

    @Test
    fun register_invalid_email_shows_error_dialog() {
        launch(RegisterActivity::class.java)

        onView(withId(R.id.firstnameEntry))
            .perform(typeText("Teszt"), closeSoftKeyboard())

        onView(withId(R.id.lastnameEntry))
            .perform(typeText("Elek"), closeSoftKeyboard())

        onView(withId(R.id.usernameEntry))
            .perform(typeText("androidteszt"), closeSoftKeyboard())

        onView(withId(R.id.emailEntry))
            .perform(typeText("rosszemail"), closeSoftKeyboard())

        onView(withId(R.id.passwordEntry))
            .perform(typeText("Titok123!"), closeSoftKeyboard())

        onView(withId(R.id.confirmEntry))
            .perform(typeText("Titok123!"), closeSoftKeyboard())

        onView(withId(R.id.registerButton)).perform(click())

        onView(withText("Érvénytelen"))
            .check(matches(isDisplayed()))
    }
}
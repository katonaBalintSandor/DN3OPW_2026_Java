package com.example.dn3opw_2026

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dn3opw_2026.activities.AdminEditEventActivity
import com.example.dn3opw_2026.model.Event
import androidx.test.espresso.action.ViewActions.scrollTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminEditEventFlowTest {

    @Test
    fun admin_edit_event_invalid_date_shows_error_dialog() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val event = Event(
            1,
            "Régi esemény",
            "Régi fejléc",
            "2026-04-17",
            "Régi leírás",
            "event.png",
            1,
            1,
            "Teszt könyvtár"
        )

        val intent = Intent(context, AdminEditEventActivity::class.java).apply {
            putExtra("admin_id", 1)
            putExtra("library_id", 1)
            putExtra("firstname", "Admin")
            putExtra("lastname", "Teszt")
            putExtra("username", "admin")
            putExtra("email", "admin@test.hu")
            putExtra("event", event)
        }

        ActivityScenario.launch<AdminEditEventActivity>(intent)

        onView(withId(R.id.titleEntry)).check(matches(isDisplayed()))

        onView(withId(R.id.dateEntry))
            .perform(replaceText("17/04/2026"), closeSoftKeyboard())

        onView(withId(R.id.dateEntry))
            .perform(replaceText("17/04/2026"), closeSoftKeyboard())

        onView(withId(R.id.saveChangesButton))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Hibás dátumformátum"))
            .check(matches(isDisplayed()))
    }
}
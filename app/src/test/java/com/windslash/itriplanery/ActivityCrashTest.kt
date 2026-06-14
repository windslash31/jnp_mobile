package com.windslash.itriplanery

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityCrashTest {
    @Test
    fun testActivityLaunch() {
        Robolectric.buildActivity(MainActivity::class.java).setup().get()
    }
}

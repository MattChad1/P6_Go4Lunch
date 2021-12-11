package com.go4lunch2.ui.list_restaurants;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.go4lunch2.TestUtils.nthChildOf;

import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;

import com.go4lunch2.TestUtils;
import com.go4lunch2.ui.main_activity.MainActivity;
import com.go4lunch2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListRestaurantsFragmentTest {

    private MainActivity activityRef;

    @Before
    public void setUp() throws Exception {
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(activity -> activityRef = activity);
        onView(withId(R.id.menu_bb_listview)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
            activityRef = null;
    }


    @Test
    public void isDetailScreenLaunched() {
        String nameRestaurant = TestUtils.getText(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
        onView(allOf(withId(R.id.item_restaurant_name), withText(nameRestaurant))).perform(click());
        onView(withId(R.id.restaurant_name)).check(matches(withText(nameRestaurant)));


    }


}
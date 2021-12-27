package com.go4lunch2.ui.list_restaurants;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.go4lunch2.TestUtils.hasItem;
import static com.go4lunch2.TestUtils.nthChildOf;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

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
    // After click on the list of restaurants, check if the name of the restaurants is correct on detail screen
    public void isDetailScreenLaunched() {
        String nameRestaurant = TestUtils.getText(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
        onView(allOf(withId(R.id.item_restaurant_name), withText(nameRestaurant))).perform(click());
        onView(withId(R.id.restaurant_name)).check(matches(withText(nameRestaurant)));
    }

    // check if workmates count increase on list when user select a restaurant to lunch
    @Test
    public void selectRestaurantTest() throws InterruptedException {

        // Select the second restaurant, to be sure the 1st one will be clear for this user
        Thread.sleep(2000);
        onView(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),1)))).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.fab_restaurant_chosen)).perform(click());
        onView(withId(R.id.backToMain)).perform(click());
        Thread.sleep(1000);
        // Check the count of workmates of the first restaurant, then select it
        String numWorkmatesPre = TestUtils.getText(allOf(withId(R.id.item_restaurant_num_workmates), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
        int workmatesValuePre = Integer.parseInt(numWorkmatesPre.substring(1, 2));
        onView(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0)))).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.fab_restaurant_chosen)).perform(click());
        onView(withId(R.id.backToMain)).perform(click());
        Thread.sleep(1000);
        // Check if count has increased
        String numWorkmatesPost = TestUtils.getText(allOf(withId(R.id.item_restaurant_num_workmates), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
        int workmatesValuePost = Integer.parseInt(numWorkmatesPost.substring(1, 2));
        assertEquals(workmatesValuePost, workmatesValuePre + 1);
    }

    //check link in nav menu
@Test
    public void checkTheLinkinNavigationDrawer() throws InterruptedException {
    Thread.sleep(2000);
    String nameRestaurantPre = TestUtils.getText(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
    onView(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0)))).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.fab_restaurant_chosen)).perform(click());
    onView(withId(R.id.backToMain)).perform(click());
    Thread.sleep(1000);
    onView(withContentDescription("Navigate up")).perform(click());
    onView(withId(R.id.menu_drawer_yourlunch)).perform(click());
    Thread.sleep(1000);
    String nameRestaurantPost = TestUtils.getText(withId(R.id.restaurant_name));
    assertEquals(nameRestaurantPost, nameRestaurantPre);
}

@Test
    public void checkListWorkmatesAfterSelection() throws InterruptedException {
        //check username
        onView(withContentDescription("Navigate up")).perform(click());
    String user = TestUtils.getText(withId(R.id.nav_drawer_name));
    Espresso.pressBack();

    // select restaurant
    String nameRestaurant = TestUtils.getText(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0))));
    onView(allOf(withId(R.id.item_restaurant_name), withParent(nthChildOf(withId(R.id.rv_list_restaurants),0)))).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.fab_restaurant_chosen)).perform(click());
    onView(withId(R.id.backToMain)).perform(click());
    Thread.sleep(1000);


    onView(withId(R.id.menu_bb_workmates)).perform(click());
    onView(withId(R.id.rv_list_workmates)).check(matches(hasItem(hasDescendant(withText(InstrumentationRegistry.getTargetContext().getResources().getString(R.string.restaurant_chosen, user, nameRestaurant))))));

}



}
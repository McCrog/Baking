/*
 * Copyright (C) 2018. The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.exoplayer2.ui.PlayerView;
import com.udacity.baking.R;
import com.udacity.baking.utilities.RecipeIdlingResource;
import com.udacity.baking.utilities.RecyclerViewMatcher;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.udacity.baking.utilities.Constants.RECIPE_ID;
import static com.udacity.baking.utilities.Constants.STEP_TAG;
import static org.hamcrest.Matchers.is;

/**
 * Created by McCrog on 06/06/2018.
 *
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailStepActivityTest {

    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String STEP_TEXT = "3. Press the cookie crumb mixture into the prepared " +
            "pie pan and bake for 12 minutes. Let crust cool to room temperature.";

    private RecipeIdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<RecipeDetailStepActivity> mActivityTestRule = new ActivityTestRule(RecipeDetailStepActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            Bundle b = new Bundle();
            b.putInt(STEP_TAG, 3);
            b.putInt(RECIPE_ID, 1);

            Intent testIntent = new Intent(mContext, RecipeDetailStepActivity.class);
            testIntent.putExtras(b);
            return testIntent;
        }
    };

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);

    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Test
    public void checkActionBarTitle() {
        String supportActionBarTitle = String.valueOf(mActivityTestRule.getActivity().getSupportActionBar().getTitle());
        Assert.assertEquals(RECIPE_NAME, supportActionBarTitle);
    }

    @Test
    public void checkStepText() {
        onView((withId(R.id.step_description_tv))).check(matches(withText(STEP_TEXT)));
    }

    @Test
    public void checkPlayerView() {
        onView(AllOf.allOf(withId(R.id.exo_play),
                withClassName(is(PlayerView.class.getName()))));
    }
}
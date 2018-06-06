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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.baking.R;
import com.udacity.baking.utilities.RecipeIdlingResource;
import com.udacity.baking.utilities.RecyclerViewMatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.udacity.baking.utilities.Constants.RECIPE_ID;

/**
 * Created by McCrog on 06/06/2018.
 *
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityTest {

    private static final String ADD_TO_WIDGET_BUTTON_TEXT = "Add ingredients to the widget";
    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String INGREDIENT_NAME = "Graham Cracker crumbs";
    private static final String INGREDIENT_QUANTITY = "2.0";
    private static final String INGREDIENT_MEASURE = "CUP";
    private static final String STEP_NAME = "Recipe Introduction";

    private RecipeIdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mActivityTestRule = new ActivityTestRule(RecipeDetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            Intent testIntent = new Intent(mContext, RecipeDetailActivity.class);
            testIntent.putExtra(RECIPE_ID, 1);
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
    public void checkAddToWidgetButtonText() {
        onView((withId(R.id.add_to_widget_button))).check(matches(withText(ADD_TO_WIDGET_BUTTON_TEXT)));
    }

    @Test
    public void checkIngredientText() {
        onView(withRecyclerView(R.id.ingredients_recycle_view)
                .atPositionOnView(0, R.id.ingredient_card_view_ingredient_tv))
                .check(matches(withText(INGREDIENT_NAME)));
    }

    @Test
    public void checkIngredientQuantityText() {
        onView(withRecyclerView(R.id.ingredients_recycle_view)
                .atPositionOnView(0, R.id.ingredient_card_view_quantity_tv))
                .check(matches(withText(INGREDIENT_QUANTITY)));
    }

    @Test
    public void checkIngredientMeasureText() {
        onView(withRecyclerView(R.id.ingredients_recycle_view)
                .atPositionOnView(0, R.id.ingredient_card_view_measure_tv))
                .check(matches(withText(INGREDIENT_MEASURE)));
    }

    @Test
    public void checkStepName() {
        onView(withRecyclerView(R.id.steps_recycle_view)
                .atPositionOnView(0, R.id.step_card_view_short_description_tv))
                .check(matches(withText(STEP_NAME)));
    }
}